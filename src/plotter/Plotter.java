package plotter;

import java.util.ArrayList;

import lejos.hardware.lcd.LCD;

public class Plotter {
	// Global resolution to be applied to drawings/movements.
	public static final int MOVEMENT_RESOLUTION = 5;

	// Constants.
	// Scale converter reference, in millimeters.
	public static final float SCALE_REF_MILLIMETERS = 100.0f;
	// Scale converter reference, in degrees.
	public static final int SCALE_REF_DEGREES = 720;

	private static ArmSystem as = null;
	private static InstructionsGenerator ig = null;
	private static ScaleConverter sc = null;

	public static void main(String[] args) {
		LCD.drawString("Initializing...", 0, 0);

		// Instantiate components.
		Plotter.as = new ArmSystem();
		Plotter.ig = new InstructionsGenerator(false);
		Plotter.sc = new ScaleConverter(Plotter.SCALE_REF_MILLIMETERS,
				Plotter.SCALE_REF_DEGREES);

		// Calibrate arms.
		// as.calibrate();

		// System ready!
		LCD.clear();
		LCD.drawString("Ready.", 0, 0);

		// DEBUG TEST.
		// 1. Receive an array of millimeters positions.
		ArrayList<FloatVector3D> millimetersPositions = new ArrayList<FloatVector3D>();
		millimetersPositions.add(new FloatVector3D(0, 0, 0));
		millimetersPositions.add(new FloatVector3D(50, 0, 0));
		millimetersPositions.add(new FloatVector3D(100, 50, 0));
		millimetersPositions.add(new FloatVector3D(50, 100, 0));
		millimetersPositions.add(new FloatVector3D(0, 50, 0));
		millimetersPositions.add(new FloatVector3D(50, 0, 0));
		// 2. Convert it to degrees positions.
		ArrayList<IntVector3D> degreesPositions = Plotter.sc
				.millimetersToDegrees(millimetersPositions);
		// 3. Generate a list of MotorInstructions from it.
		ArrayList<MotorInstruction> instructions = Plotter.ig
				.getInstructions(degreesPositions);
		// 4. Fire in the hole.
		Plotter.as.executeInstructions(instructions);
		// END OF DEBUG.
	}
}
