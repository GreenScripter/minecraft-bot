package greenscripter.minecraft.packet.s2c.play;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerInfoUpdatePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_info_update");

	public byte actions;
	public Map<UUID, Action> toPerform = new HashMap<>();

	public PlayerInfoUpdatePacket() {}

	public PlayerInfoUpdatePacket(byte actions, Map<UUID, Action> toPerform) {
		this.actions = actions;
		this.toPerform = toPerform;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(actions);
		out.writeVarInt(toPerform.size());
		for (var e : toPerform.entrySet()) {
			out.writeUUID(e.getKey());
			e.getValue().toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		actions = in.readByte();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			UUID uuid = in.readUUID();
			Action a = new Action(actions);
			a.fromBytes(in);
			toPerform.put(uuid, a);
		}
	}

	public static final byte ADD_PLAYER = 0x01;
	public static final byte INIT_CHAT = 0x02;
	public static final byte UPDATE_GAME_MODE = 0x04;
	public static final byte UPDATE_LISTED = 0x08;
	public static final byte UPDATE_LATENCY = 0x10;
	public static final byte UPDATE_DISPLAY_NAME = 0x20;
	public static final byte ALL = ADD_PLAYER | INIT_CHAT | UPDATE_GAME_MODE | UPDATE_LISTED | UPDATE_LATENCY | UPDATE_DISPLAY_NAME;

	public static final byte[] ACTIONS = { ADD_PLAYER, INIT_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME };
	public static final List<Function<MCInputStream, ActionStep>> DECODE = List.of(//
			ActionAddPlayer::new,//
			ActionInitChat::new, //
			ActionUpdateGameMode::new,//
			ActionUpdateListed::new,//
			ActionUpdatePing::new,//
			ActionDisplayName::new//
	);
	public static final List<Supplier<ActionStep>> CREATE = List.of(//
			ActionAddPlayer::new,//
			ActionInitChat::new, //
			ActionUpdateGameMode::new,//
			ActionUpdateListed::new,//
			ActionUpdatePing::new,//
			ActionDisplayName::new//
	);

	public static class Action {

		public byte actions;
		public List<ActionStep> steps = new ArrayList<>();

		public Action(byte actions) {
			this.actions = actions;
		}

		public void toBytes(MCOutputStream out) throws IOException {
			for (ActionStep step : steps) {
				step.toBytes(out);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			for (int i = 0; i < ACTIONS.length; i++) {
				if ((actions & ACTIONS[i]) != 0) {
					ActionStep step = DECODE.get(i).apply(in);
					steps.add(step);
				}
			}
		}

		public void updateTo(Action other) {
			List<ActionStep> stepsNew = new ArrayList<>();
			create: for (var c : CREATE) {
				ActionStep step = c.get();
				for (var a : other.steps) {
					if (a.getClass().isAssignableFrom(step.getClass())) {
						stepsNew.add(a);
						continue create;
					}
				}
				for (var a : steps) {
					if (a.getClass().isAssignableFrom(step.getClass())) {
						stepsNew.add(a);
						continue create;
					}
				}
				stepsNew.add(step);
			}
			actions = ALL;
			steps = stepsNew;
		}

		public String toString() {
			return "Action [actions=" + actions + ", " + (steps != null ? "steps=" + steps : "") + "]";
		}

	}

	public abstract static class ActionStep {

		public ActionStep() {}

		public ActionStep(MCInputStream in) {}

		public abstract void toBytes(MCOutputStream out) throws IOException;

		public abstract void fromBytes(MCInputStream in) throws IOException;
	}

	public static class ActionAddPlayer extends ActionStep {

		public String name = "Placeholder";
		public List<Properties> props = new ArrayList<>();

		public ActionAddPlayer() {

		}

		public ActionAddPlayer(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeString(name);
			out.writeVarInt(props.size());
			for (Properties p : props) {
				out.writeString(p.name);
				out.writeString(p.value);
				out.writeBoolean(p.isSigned);
				if (p.isSigned) {
					out.writeString(p.signature);
				}
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			name = in.readString();
			int length = in.readVarInt();
			for (int i = 0; i < length; i++) {
				Properties p = new Properties();
				p.name = in.readString();
				p.value = in.readString();
				p.isSigned = in.readBoolean();
				if (p.isSigned) {
					p.signature = in.readString();
				}
				props.add(p);
			}

		}

		public static class Properties {

			public String name;
			public String value;
			public boolean isSigned;
			public String signature;

			public String toString() {
				return "Properties [" + (name != null ? "name=" + name + ", " : "") + (value != null ? "value=" + value + ", " : "") + "isSigned=" + isSigned + ", " + (signature != null ? "signature=" + signature : "") + "]";
			}

		}

		public String toString() {
			return "ActionAddPlayer [" + (name != null ? "name=" + name + ", " : "") + (props != null ? "props=" + props : "") + "]";
		}

	}

	public static class ActionInitChat extends ActionStep {

		public boolean hasSignature = false;
		public UUID sessionId;

		public long expires;
		public byte[] key;
		public byte[] keySignature;

		public ActionInitChat() {}

		public ActionInitChat(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeBoolean(hasSignature);
			if (hasSignature) {
				out.writeUUID(sessionId);
				out.writeLong(expires);
				out.writeVarInt(key.length);
				out.write(key);
				out.writeVarInt(keySignature.length);
				out.write(keySignature);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			hasSignature = in.readBoolean();
			if (hasSignature) {
				sessionId = in.readUUID();
				expires = in.readLong();
				key = new byte[in.readVarInt()];
				in.readFully(key);
				keySignature = new byte[in.readVarInt()];
				in.readFully(keySignature);
			}
		}

		public String toString() {
			return "ActionInitChat [hasSignature=" + hasSignature + ", " + (sessionId != null ? "sessionId=" + sessionId + ", " : "") + "expires=" + expires + ", " + (key != null ? "key=" + Arrays.toString(key) + ", " : "") + (keySignature != null ? "keySignature=" + Arrays.toString(keySignature) : "") + "]";
		}

	}

	public static class ActionUpdateGameMode extends ActionStep {

		public int gameMode = 0;

		public ActionUpdateGameMode() {}

		public ActionUpdateGameMode(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(gameMode);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			gameMode = in.readVarInt();
		}

		public String toString() {
			return "ActionUpdateGameMode [gameMode=" + gameMode + "]";
		}
	}

	public static class ActionUpdateListed extends ActionStep {

		public boolean listed = true;

		public ActionUpdateListed() {}

		public ActionUpdateListed(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeBoolean(listed);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			listed = in.readBoolean();
		}
	}

	public static class ActionUpdatePing extends ActionStep {

		public int ping = 0;

		public ActionUpdatePing() {}

		public ActionUpdatePing(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(ping);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			ping = in.readVarInt();
		}
	}

	public static class ActionDisplayName extends ActionStep {

		public boolean hasDisplayName = false;
		public NBTComponent displayName;

		public ActionDisplayName() {}

		public ActionDisplayName(MCInputStream in) {
			super(in);
			try {
				fromBytes(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeBoolean(hasDisplayName);
			if (hasDisplayName) {
				out.writeNBT(displayName);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			hasDisplayName = in.readBoolean();
			if (hasDisplayName) {
				displayName = in.readNBT();
			}
		}
	}
}
