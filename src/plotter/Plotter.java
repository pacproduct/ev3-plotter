package plotter;

import lejos.hardware.lcd.LCD;

public class Plotter {
	// Global resolution to be applied to drawings/movements.
	private static final int MOVEMENT_RESOLUTION = 5;

	public static void main(String[] args) {
		LCD.drawString("Calibrating...", 0, 0);

		// Initialize and calibrate arms.
		ArmSystem as = new ArmSystem();

		// Initialize geometry utility class.
		GeometryInstructions geometry = new GeometryInstructions(
				MOVEMENT_RESOLUTION);

		// Calibrate.
		// as.calibrate();

		// Tests.
		LCD.drawString("Running.", 0, 0);

		as.armSyncTest();

		// Square
		// as.executeInstructions(geometry.getLineInstructions(new Vector3D(0,
		// 0,
		// 0), new Vector3D(1000, 0, 0)));
		// as.executeInstructions(geometry.getLineInstructions(new
		// Vector3D(1000,
		// 0, 0), new Vector3D(1000, 1000, 0)));

		// ArrayList<MotorInstruction> instructions = geometry
		// .getHorizontalCircleInstructions(new Vector3D(1000, 2000, 0),
		// 500, -90, 8);
		//
		// LCD.clear();
		// LCD.drawString("Num instr.:" + instructions.size(), 0, 0);
		//
		// Delay.msDelay(1000);

		// as.executeInstructions(instructions);

		// Goto
		// as.safeRotateTo(1000, 0, 0);
		// as.safeRotateTo(1000, 500, 0);

		// EV3LargeRegulatedMotor motorX = new
		// EV3LargeRegulatedMotor(MotorPort.A);
		// EV3LargeRegulatedMotor motorY = new
		// EV3LargeRegulatedMotor(MotorPort.B);
		//
		// motorX.synchronizeWith(new BaseRegulatedMotor[] { motorY });
		// motorX.startSynchronization();
		// motorX.setSpeed(50);
		// motorY.setSpeed(50);
		// motorX.rotateTo(180, true);
		// motorY.rotateTo(180, false);
		// motorX.endSynchronization();
		// motorX.waitComplete();
		// motorY.waitComplete();

	}
}
