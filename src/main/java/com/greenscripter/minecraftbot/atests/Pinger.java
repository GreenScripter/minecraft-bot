package com.greenscripter.minecraftbot.atests;

import java.io.IOException;
import java.net.Socket;

import com.greenscripter.minecraftbot.packet.c2s.handshake.HandshakePacket;
import com.greenscripter.minecraftbot.packet.c2s.status.PingRequestPacket;
import com.greenscripter.minecraftbot.packet.c2s.status.StatusRequestPacket;
import com.greenscripter.minecraftbot.packet.s2c.status.PingResponsePacket;
import com.greenscripter.minecraftbot.packet.s2c.status.StatusResponsePacket;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

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
