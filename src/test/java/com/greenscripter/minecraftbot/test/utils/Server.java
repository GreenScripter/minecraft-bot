package com.greenscripter.minecraftbot.test.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Server implements Closeable {

	String name;
	String host;
	int port;
	File folder;
	File sourceFolder;
	boolean started;
	boolean closed;

	Process serverProcess;
	InputStream in;
	InputStream err;
	OutputStream out;

	Deque<String> serverOutput = new ArrayDeque<>();
	boolean collectOutput = false;

	Map<Consumer<String>, Void> messageHandlers = new IdentityHashMap<Consumer<String>, Void>();
	Map<Function<String, Boolean>, Void> tempMessageHandlers = new IdentityHashMap<Function<String, Boolean>, Void>();
	Signal<Void> fullyStarted;

	public Server(String name, int port) {
		this.name = name;
		this.host = "localhost";
		this.port = port;
		this.folder = new File("testing/" + name);
		this.sourceFolder = new File("testing/template");
		this.fullyStarted = new Signal<>();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Consumer<String> messageHandler(Consumer<String> handler) {
		messageHandlers.put(handler, null);
		return handler;
	}

	public void removeMessageHandler(Consumer<String> handler) {
		messageHandlers.remove(handler);
	}

	public Function<String, Boolean> tempMessageHandler(Function<String, Boolean> handler) {
		synchronized (tempMessageHandlers) {
			tempMessageHandlers.put(handler, null);
		}
		return handler;
	}

	public void removeTempMessageHandler(Function<String, Boolean> handler) {
		synchronized (tempMessageHandlers) {
			tempMessageHandlers.remove(handler);
		}
	}

	private void handleOutput(String s) {
		if (!fullyStarted.sent()) {
			if (s.matches(".*Done \\([0-9.]*s\\)! For help, type \"help\".*")) {
				fullyStarted.send();
			}
		}
		for (Consumer<String> messageHandler : messageHandlers.keySet()) {
			if (messageHandler != null) {
				try {
					messageHandler.accept(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		synchronized (tempMessageHandlers) {
			List<Function<String, Boolean>> toRemove = new ArrayList<>();
			for (Function<String, Boolean> messageHandler : tempMessageHandlers.keySet()) {
				if (messageHandler != null) {
					try {
						if (messageHandler.apply(s)) {
							toRemove.add(messageHandler);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (tempMessageHandlers.isEmpty()) {
				synchronized (serverOutput) {
					serverOutput.add(s);
				}
			}
			toRemove.forEach(tempMessageHandlers::remove);
		}
	}

	private void handleError(String s) {}

	public void sendCommand(String s) throws IOException {
		out.write(s.getBytes());
		out.write('\n');
		out.flush();
	}

	public void open() throws Exception {
		if (started) return;
		if (closed) throw new Exception("Already closed.");
		started = true;
		FileUtils.delete(folder);
		FileUtils.copy(sourceFolder, folder);

		Properties props = new Properties();
		props.load(new FileReader(new File(folder, "server.properties")));
		props.put("server-port", "" + port);
		props.store(new FileWriter(new File(folder, "server.properties")), "");

		File start = new File(folder, "start.sh");
		start.setExecutable(true);
		serverProcess = Runtime.getRuntime().exec(new String[] { "bash", "-c", start.getCanonicalPath() }, null, folder);
		err = serverProcess.getErrorStream();
		in = serverProcess.getInputStream();
		out = serverProcess.getOutputStream();

		Thread.startVirtualThread(() -> {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					handleOutput(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread.startVirtualThread(() -> {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(err));
				String line;
				while ((line = reader.readLine()) != null) {
					handleError(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public boolean waitForStart(int timeout) throws InterruptedException {
		fullyStarted.waitFor(timeout);
		return fullyStarted.sent();
	}

	public void waitForOutputContains(int wait, String match) throws InterruptedException {
		waitForOutputMatches(wait, s -> s.contains(match));
	}

	public void waitForOutputMatches(int wait, String match) throws InterruptedException {
		waitForOutputMatches(wait, s -> s.matches(match));
	}

	public void waitForOutputContains(int wait, List<String> match) throws InterruptedException {
		waitForOutputMatchesAll(wait, match.stream().map(s -> (Predicate<String>) c -> c.contains(s)).toList());
	}

	public void waitForOutputMatches(int wait, List<String> match) throws InterruptedException {
		waitForOutputMatchesAll(wait, match.stream().map(s -> (Predicate<String>) c -> c.matches(s)).toList());
	}

	public void waitForOutputMatchesAll(int wait, List<Predicate<String>> match) throws InterruptedException {
		boolean[] met = new boolean[match.size()];
		waitForOutputMatches(wait, s -> {
			boolean anyNotMet = false;
			for (int i = 0; i < match.size(); i++) {
				if (met[i]) continue;
				if (match.get(i).test(s)) {
					met[i] = true;
				} else {
					anyNotMet = true;
				}
			}
			if (anyNotMet) {
				return false;
			}
			return true;
		});
	}

	public void waitForOutputMatches(int wait, Predicate<String> match) throws InterruptedException {
		var result = new Signal<>();
		var handler = this.tempMessageHandler(s -> {
			if (!result.sent() && match.test(s)) {
				result.send();
				return true;
			}
			return false;
		});
		while (!serverOutput.isEmpty()) {
			var line = serverOutput.removeFirst();
			if (match.test(line)) {
				return;
			}
		}
		try {
			result.waitFor(wait);
			if (!result.sent()) {
				throw new RuntimeException("Waited more than " + wait + " ms.");
			}
		} finally {
			removeTempMessageHandler(handler);
		}
	}

	public void print() {
		this.messageHandler(System.out::println);
	}

	public void close() throws IOException {
		if (closed) return;
		try {
			serverProcess.toHandle().descendants().forEach(h -> h.destroyForcibly());
			serverProcess.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtils.delete(folder);
		closed = true;
	}

}
