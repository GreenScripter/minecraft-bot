package greenscripter.minecraft.utils;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class GZIP {

	public static byte[] gzip(byte[] input) {
		GZIPOutputStream gzipOS = null;
		try {
			ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			gzipOS = new GZIPOutputStream(byteArrayOS);
			gzipOS.write(input);
			gzipOS.flush();
			gzipOS.close();
			return byteArrayOS.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e); // <-- just a RuntimeException
		} finally {
			if (gzipOS != null) {
				try {
					gzipOS.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	public static byte[] gunzip(byte[] input) {
		GZIPInputStream gzipOS = null;
		try {
			ByteArrayInputStream byteArrayOS = new ByteArrayInputStream(input);
			gzipOS = new GZIPInputStream(byteArrayOS);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[8192];
			int read = 0;
			while ((read = gzipOS.read(b)) != -1) {
				out.write(b, 0, read);
			}
			gzipOS.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e); // <-- just a RuntimeException
		} finally {
			if (gzipOS != null) {
				try {
					gzipOS.close();
				} catch (Exception ignored) {
				}
			}
		}
	}
}
