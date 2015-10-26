package ev3Plotter;

public class MotorInstruction {
	// Move types.
	final static int MOVE_NONE = 0;
	final static int MOVE_X = 1;
	final static int MOVE_Y = 2;
	final static int MOVE_Z = 3;
	final static int MOVE_MIXED = 4;

	public int moveX = 0;
	public int moveY = 0;
	public int moveZ = 0;

	public float speedRatioX = 1;
	public float speedRatioY = 1;
	public float speedRatioZ = 1;

	public MotorInstruction() {
	}

	public MotorInstruction(int moveX, int moveY, int moveZ) {
		this.moveX = moveX;
		this.moveY = moveY;
		this.moveZ = moveZ;
	}

	public MotorInstruction(int moveX, int moveY, int moveZ, float speedRatioX,
			float speedRatioY, float speedRatioZ) {
		this.moveX = moveX;
		this.moveY = moveY;
		this.moveZ = moveZ;

		this.speedRatioX = speedRatioX;
		this.speedRatioY = speedRatioY;
		this.speedRatioZ = speedRatioZ;
	}

	public int getMoveType() {
		if (moveX != 0 && moveY == 0 && moveZ == 0) {
			return MOVE_X;
		}

		if (moveX == 0 && moveY != 0 && moveZ == 0) {
			return MOVE_Y;
		}

		if (moveX == 0 && moveY == 0 && moveZ != 0) {
			return MOVE_Z;
		}

		if (moveX == 0 && moveY == 0 && moveZ == 0) {
			return MOVE_NONE;
		}

		return MOVE_MIXED;
	}

	public String toString() {
		String move = "MoveX: " + this.moveX + ", MoveY: " + this.moveY
				+ ", MoveZ: " + this.moveZ;
		String speed = "SpeedRatioX: " + this.speedRatioX + ", SpeedRatioY: "
				+ this.speedRatioY + ", SpeedRatioZ: " + this.speedRatioZ;
		return move + ", " + speed;
	}
}
