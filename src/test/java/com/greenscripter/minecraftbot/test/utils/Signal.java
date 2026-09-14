package com.greenscripter.minecraftbot.test.utils;

public class Signal<V> {

	boolean sent;

	Object lock = new Object();

	V result;

	public void send() {
		synchronized (lock) {
			sent = true;
			lock.notifyAll();
		}
	}

	public void send(V result) {
		synchronized (lock) {
			this.result = result;
			sent = true;
			lock.notifyAll();
		}
	}

	public boolean sent() {
		return sent;
	}

	public V sentValue() {
		return result;
	}

	public void waitFor(int timeout) throws InterruptedException {
		if (sent) return;
		synchronized (lock) {
			lock.wait(timeout);
		}
	}
}
