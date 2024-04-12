package greenscripter.minecraft.utils;

import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ZLib {

	public static byte[] compress(byte[] b) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DeflaterOutputStream dos = new DeflaterOutputStream(baos);
			dos.write(b);
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	public static byte[] decompress(byte[] b) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			InflaterInputStream iis = new InflaterInputStream(bais);

			byte[] buf = new byte[1024];
			int read = -1;
			while ((read = iis.read(buf)) != -1) {
				baos.write(buf, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
}
