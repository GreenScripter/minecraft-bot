package greenscripter.remoteindicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import greenscripter.minecraft.utils.Vector;

public class IndicatorServer {

	Map<Integer, Shape> shapes = new ConcurrentHashMap<>();
	List<DataOutputStream> clients = Collections.synchronizedList(new ArrayList<>());
	LinkedBlockingQueue<Update> outgoingShapes = new LinkedBlockingQueue<>();
	int shapeId = 0;
	int version = 1;
	ServerSocket ss;

	private static final int ID_ADD_LINE = 0;
	private static final int ID_ADD_CUBOID = 1;
	private static final int ID_REMOVE_SHAPE = 2;

	public IndicatorServer(int port) throws IOException {
		ss = new ServerSocket(port);
		new Thread(() -> {
			try {
				while (true) {
					Socket s = ss.accept();
					new Thread(() -> {
						DataOutputStream out = null;
						try {
							out = new DataOutputStream(s.getOutputStream());
							out.writeInt(version);
							clients.add(out);
							synchronized (clients) {
								for (var shape : shapes.entrySet()) {
									if (shape.getValue() instanceof Cuboid c) {
										writeCuboid(out, shape.getKey(), c);
									} else if (shape.getValue() instanceof Line c) {
										writeLine(out, shape.getKey(), c);
									}
								}
							}
							DataInputStream in = new DataInputStream(s.getInputStream());
							while (true) {
								int type = in.readInt();
								switch (type) {

								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (out != null) clients.remove(out);
						}

					}).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				while (true) {
					Update u = outgoingShapes.take();
					synchronized (clients) {
						for (DataOutputStream out : clients) {
							if (u.delete) {
								try {
									out.writeInt(ID_REMOVE_SHAPE);
									out.writeInt(u.id);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else if (u.s instanceof Line l) {
								writeLine(out, u.id, l);
							} else if (u.s instanceof Cuboid l) {
								writeCuboid(out, u.id, l);
							}
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static int getColor(int r, int g, int b, int a) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
	}

	public int addLine(String dimension, Vector pos1, Vector pos2, int color) {
		int id = shapeId++;
		setLine(id, dimension, pos1, pos2, color);
		return id;
	}

	public void setLine(int id, String dimension, Vector pos1, Vector pos2, int color) {
		Line l = new Line();
		l.pos1 = pos1;
		l.pos2 = pos2;
		l.color = color;
		l.dimension = dimension;
		l.depthTest = false;

		shapes.put(id, l);

		outgoingShapes.add(new Update(id, l, false));
	}

	private void writeLine(DataOutputStream out, int id, Line l) {
		try {
			out.writeInt(ID_ADD_LINE);
			out.writeInt(id);

			out.writeDouble(l.pos1.x);
			out.writeDouble(l.pos1.y);
			out.writeDouble(l.pos1.z);

			out.writeDouble(l.pos2.x);
			out.writeDouble(l.pos2.y);
			out.writeDouble(l.pos2.z);

			out.writeUTF(l.dimension);
			out.writeInt(l.color);
			out.writeBoolean(l.depthTest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int addCuboid(String dimension, Vector pos1, Vector pos2, int color) {
		int id = shapeId++;
		setCuboid(id, dimension, pos1, pos2, color);
		return id;
	}

	public void setCuboid(int id, String dimension, Vector pos1, Vector pos2, int color) {
		Cuboid l = new Cuboid();
		l.pos1 = pos1;
		l.pos2 = pos2;
		l.color = color;
		l.dimension = dimension;
		l.depthTest = false;

		shapes.put(id, l);

		outgoingShapes.add(new Update(id, l, false));
	}

	private void writeCuboid(DataOutputStream out, int id, Cuboid l) {
		try {
			out.writeInt(ID_ADD_CUBOID);
			out.writeInt(id);

			out.writeDouble(l.pos1.x);
			out.writeDouble(l.pos1.y);
			out.writeDouble(l.pos1.z);

			out.writeDouble(l.pos2.x);
			out.writeDouble(l.pos2.y);
			out.writeDouble(l.pos2.z);

			out.writeUTF(l.dimension);
			out.writeInt(l.color);
			out.writeBoolean(l.depthTest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeShape(int id) {
		shapes.remove(id);
		outgoingShapes.add(new Update(id, null, true));
	}

	public Set<Integer> getShapes() {
		synchronized (shapes) {
			return new HashSet<>(shapes.keySet());
		}
	}

	public Shape getShape(int id) {
		return shapes.get(id);
	}

	record Update(int id, Shape s, boolean delete) {

	}
}
