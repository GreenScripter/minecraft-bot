package greenscripter.minecraft.packet.s2c.status;

public class PingData {

	public Version version;
	public Players players;
	public Description description;
	public String favicon;

	static public class Description {

		public String text;
		public String color;
		public Description[] extra;

		public Description() {}

		public Description(String text) {
			this.text = text;
		}

		public String toString() {
			String s = text;
			for (Description d : extra) {
				s += d.text;
			}
			return s;
		}
	}

	static public class Version {

		public String name;
		public int protocol;

		public Version() {}

		public Version(String name, int protocol) {
			this.name = name;
			this.protocol = protocol;
		}

	}

	static public class Players {

		public int max;
		public int online;
		public Sample[] sample;

		public Players() {}

		public Players(int max, int online) {
			this.max = max;
			this.online = online;
		}

		public Players(int max, int online, Sample[] sample) {
			this.max = max;
			this.online = online;
			this.sample = sample;
		}

	}

	static public class Sample {

		public String name;
		public String id;

		public Sample() {}

		public Sample(String name, String id) {
			this.name = name;
			this.id = id;
		}

	}
}
