package greenscripter.minecraft.nbt;

import java.util.zip.GZIPInputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTParseTest {

	public static void main(String[] args) throws Exception {
		var in = new MCInputStream(new GZIPInputStream(new FileInputStream("test.dat")));
		//		System.out.println(in.readByte());
		//		NBTTagCompound c = NBTComponent.readNBT(in);
		NBTTagByteArray bytes = new NBTTagByteArray();
		bytes.data = new byte[1024 * 10];
		NBTTagCompound c = new NBTTagCompound("", bytes);
		//		System.out.println(c);
		var out = new MCOutputStream((new FileOutputStream("testout.nbt")));
		NBTComponent.writeNBT(out, c);
		out.flush();
		out.close();
	}

}
