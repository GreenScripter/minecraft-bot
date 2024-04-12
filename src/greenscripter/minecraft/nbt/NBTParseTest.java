package greenscripter.minecraft.nbt;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTParseTest {

	public static void main(String[] args) throws Exception {
		var in = new MCInputStream(new GZIPInputStream(new FileInputStream("test.dat")));
		//		System.out.println(in.readByte());
		NBTTagCompound c = NBTComponent.readNBT(in);
		System.out.println(c);
		var out = new MCOutputStream(new GZIPOutputStream(new FileOutputStream("testout.dat")));
		NBTComponent.writeNBT(out, c);
		out.flush();
		out.close();
	}

}
