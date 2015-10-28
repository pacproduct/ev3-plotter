package common;

import java.util.ArrayList;

public class NetComPacket {
	// Constants: Packet types.
	// Types that do not need values.
	public static final int TYPE_NULL = 0;
	public static final int TYPE_EXIT = 1;
	public static final int TYPE_RUN_PENDING_ACTIONS = 2;
	// Integer types.
	public static final int TYPE_SET_SPEED = 100;
	// String types.
	public static final int TYPE_DISPLAY_TEXT = 200;
	// FloatVector3D list types.
	public static final int TYPE_STACK_MILLIMETER_POSITIONS = 300;

	// Packet type.
	public int type = NetComPacket.TYPE_NULL;

	// Integer content.
	public Integer integerValue = 0;
	// String content.
	public String stringValue = null;
	// FloatVector3D content.
	public ArrayList<FloatVector3D> floatVector3DList = new ArrayList<FloatVector3D>();

	public NetComPacket() {
	}

	public NetComPacket(int type) {
		this.type = type;
	}

	/**
	 * Constructor.
	 *
	 * @param type
	 *            Packet type.
	 * @param value
	 *            Packet value.
	 */
	public NetComPacket(int type, Integer value) {
		this(type);
		this.integerValue = value;
	}

	public NetComPacket(int type, String value) {
		this(type);
		this.stringValue = value;
	}

	public NetComPacket(int type, ArrayList<FloatVector3D> value) {
		this(type);
		this.floatVector3DList = (ArrayList<FloatVector3D>) value;
	}
}
