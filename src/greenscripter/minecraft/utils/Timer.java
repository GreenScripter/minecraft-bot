package greenscripter.minecraft.utils;

public class Timer {

	static long[] stack = new long[1024];
	static boolean[] stackRunning = new boolean[1024];
	static String[] stackNames = new String[1024];

	static int index = -1;

	public static void start(String name) {
		index++;
		stack[index] = System.nanoTime();
		stackNames[index] = name;
		stackRunning[index] = true;
	}

	public static void create(String name) {
		index++;
		stack[index] = 0;
		stackNames[index] = name;
		stackRunning[index] = false;
	}

	public static long stop(long threshold) {
		long time = stackRunning[index] ? (System.nanoTime() - stack[index]) / 1000 : stack[index] / 1000;
		String name = stackNames[index];
		index--;
		if (threshold < 0) return time;
		if (time > threshold) {
			System.out.println(name + ": " + time + " μs");
		}
		return time;
	}

	public static long check(String extra, long threshold) {
		long time = stackRunning[index] ? (System.nanoTime() - stack[index]) / 1000 : stack[index] / 1000;
		String name = stackNames[index];
		if (time > threshold) System.out.println(name + " " + extra + ": " + time + " μs");
		return time;
	}

	public static long get() {
		return stackRunning[index] ? (System.nanoTime() - stack[index]) / 1000 : stack[index] / 1000;
	}

	public static void pause(int offset) {
		long time = System.nanoTime();
		int v = index - offset;
		if (stackRunning[v]) {
			stackRunning[v] = false;
			stack[v] = (time - stack[v]);
		}
	}

	public static void unpause(int offset) {
		int v = index - offset;
		if (!stackRunning[v]) {
			stackRunning[v] = true;
			stack[v] = (System.nanoTime() - stack[v]);
		}
	}

}
