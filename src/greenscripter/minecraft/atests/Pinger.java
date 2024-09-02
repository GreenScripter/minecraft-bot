package greenscripter.minecraft.atests;

import java.io.IOException;
import java.net.Socket;

import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.status.PingRequestPacket;
import greenscripter.minecraft.packet.c2s.status.StatusRequestPacket;
import greenscripter.minecraft.packet.s2c.status.PingResponsePacket;
import greenscripter.minecraft.packet.s2c.status.StatusResponsePacket;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class Pinger {

	public static void main(String[] args) throws Exception {
		var ping = ping("localhost", 20255);
		System.out.println(ping);
	}

	@SuppressWarnings("resource")
	public static PingResponse ping(String host, int port) throws IOException {
		Socket s = new Socket(host, port);

		var in = new MCInputStream(s.getInputStream());
		var out = new MCOutputStream(s.getOutputStream());

		out.writePacket(new HandshakePacket(host, port, 1));

		out.writePacket(new StatusRequestPacket());

		var resp = in.readPacket(new StatusResponsePacket());

		long start = System.currentTimeMillis();
		out.writePacket(new PingRequestPacket(start));

		var pingResp = in.readPacket(new PingResponsePacket());

		long ping = System.currentTimeMillis() - pingResp.value;
		long pingReal = System.currentTimeMillis() - start;

		s.close();
		return new PingResponse(resp.value, ping, pingReal);
	}

	public static record PingResponse(String value, long ping, long pingReal) {}

}
