package common;

import ev3Plotter.FloatVector3D;

public class NetComPacket {
	// Constants: Packet types.
	// Types that do not need values.
	public static final int TYPE_NULL = 0;
	public static final int TYPE_EXIT = 1;
	// Integer types.
	public static final int TYPE_SET_SPEED = 100;
	// String types.
	public static final int TYPE_DISPLAY_TEXT = 200;
	// FloatVector3D types.
	public static final int TYPE_GO_TO_POSITION = 300;

	// Packet type.
	public int type = NetComPacket.TYPE_NULL;

	// Integer content.
	public Integer integerContent = 0;
	// String content.
	public String stringContent = null;
	// FloatVector3D content.
	public FloatVector3D floatVector3DContent = null;

	/**
	 * Constructor.
	 *
	 * @param type
	 *            Packet type.
	 * @param value
	 *            Packet value.
	 */
	public NetComPacket(int type, Object value) {
		this.type = type;

		if (value instanceof Integer) {
			this.integerContent = (Integer) value;
		} else if (value instanceof String) {
			this.stringContent = (String) value;
		} else if (value instanceof FloatVector3D) {
			this.floatVector3DContent = (FloatVector3D) value;
		}
	}
}
