package plotter;

public class MotorInstruction {
	final static int DO_NOTHING = 0;

	final static int MOVE_X = 1;
	final static int MOVE_Y = 2;
	final static int MOVE_Z = 3;

	public int action = DO_NOTHING;
	public int value = 0;

	public MotorInstruction(int action) {
		this.action = action;
	}

	public MotorInstruction(int action, int value) {
		this.action = action;
		this.value = value;
	}
}
