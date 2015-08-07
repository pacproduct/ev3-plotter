package plotter;

import java.util.ArrayList;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

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

		// Square
		// as.executeInstructions(geometry.getLineInstructions(new Vector3D(0,
		// 0,
		// 0), new Vector3D(1000, 0, 0)));
		// as.executeInstructions(geometry.getLineInstructions(new
		// Vector3D(1000,
		// 0, 0), new Vector3D(1000, 1000, 0)));

		ArrayList<MotorInstruction> instructions = geometry
				.getHorizontalCircleInstructions(new Vector3D(1000, 2000, 0),
						500, -90, 64);

		LCD.clear();
		LCD.drawString("Num instr.:" + instructions.size(), 0, 0);

		Delay.msDelay(1000);

		// as.executeInstructions(instructions);

		// Goto
		// as.safeRotateTo(1000, 0, 0);
		// as.safeRotateTo(1000, 500, 0);

		/*
		 * EV3LargeRegulatedMotor motorX = new
		 * EV3LargeRegulatedMotor(MotorPort.A); EV3LargeRegulatedMotor motorY =
		 * new EV3LargeRegulatedMotor(MotorPort.B);
		 *
		 * motorX.synchronizeWith(new EV3LargeRegulatedMotor[] { motorY });
		 * motorX.startSynchronization(); motorX.setSpeed(1);
		 * motorY.setSpeed(20); motorX.rotate(20); motorY.rotate(400);
		 * motorX.endSynchronization(); motorX.waitComplete();
		 * motorY.waitComplete();
		 */

		/*
		 * ArmMotor armMotor = new ArmMotor( new Port[] { MotorPort.A,
		 * MotorPort.B }, true, true);
		 *
		 * armMotor.setSpeed(200);
		 *
		 * armMotor.setMaximumPosition(360); armMotor.setMinimumPosition(-360);
		 *
		 * armMotor.forward(false); Delay.msDelay(500);
		 * armMotor.backward(false);
		 *
		 * LCD.clear(); LCD.drawString("Final pos: " + armMotor.getPosition(),
		 * 0, 0);
		 *
		 * Delay.msDelay(5000);
		 */

	}
}
