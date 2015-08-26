package plotter;

import java.util.ArrayList;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class ArmSystem {
	// Constants.
	// Boolean set to TRUE if motors should rotate in the normal direction, or
	// FALSE if they should behave conversely.
	public static final boolean MOTOR_X_DIRECTION = false;
	public static final boolean MOTOR_Y_DIRECTION = false;
	// Motor speed when calibrating.
	public static final int CALIBRATION_SPEED = 400;
	// Motor speed when operating.
	public static final int NORMAL_OPERATION_SPEED = 200;
	// Angle to rotate motors just after going backward until sensor got
	// pressed, to move them to position zero.
	public static final int ANGLE_X_TO_POS_ZERO = 50;
	public static final int ANGLE_Y_TO_POS_ZERO = 50;
	// Maximums.
	public static final int MAX_POS_X = 2000;
	public static final int MAX_POS_Y = 2000;
	public static final int MAX_POS_Z = 400;

	// Robotic parts and properties.
	private ArmMotor armMotorX;
	private ArmMotor armMotorY;
	private SampleProvider sensorX;
	private SampleProvider sensorY;
	private float[] sensorSampleX;
	private float[] sensorSampleY;

	/**
	 * Constructor.
	 */
	public ArmSystem() {
		this.armMotorX = new ArmMotor(new Port[] { MotorPort.A },
				MOTOR_X_DIRECTION, false);
		this.armMotorY = new ArmMotor(new Port[] { MotorPort.B, MotorPort.C },
				MOTOR_Y_DIRECTION, true);

		this.armMotorX.setSpeed(NORMAL_OPERATION_SPEED);
		this.armMotorY.setSpeed(NORMAL_OPERATION_SPEED);

		// Set min/max positions.
		this.armMotorX.setMinimumPosition(0);
		this.armMotorX.setMaximumPosition(MAX_POS_X);

		this.armMotorY.setMinimumPosition(0);
		this.armMotorY.setMaximumPosition(MAX_POS_Y);

		this.sensorX = new EV3TouchSensor(SensorPort.S1);
		this.sensorY = sensorX;

		this.sensorSampleX = new float[this.sensorX.sampleSize()];
		this.sensorSampleY = new float[this.sensorY.sampleSize()];

		// TODO Sames for Z.
	}

	/**
	 * Calibrates arms, and moves them to their zero position.
	 */
	public void calibrate() {
		// -- Calibrate motor X --

		this.armMotorX.setSpeed(CALIBRATION_SPEED);

		// Move back until sensor gets pressed.
		this.armMotorX.backward(false);

		// When the sensor is pressed, stop calibration and bounce back to what
		// will become position zero.
		this.sensorX.fetchSample(this.sensorSampleX, 0);
		while (this.sensorSampleX[0] == 0) {
			this.sensorX.fetchSample(this.sensorSampleX, 0);
		}
		// Brake motor.
		this.armMotorX.stop();
		this.armMotorX.setSpeed(NORMAL_OPERATION_SPEED);
		// Move to final position zero.
		this.armMotorX.rotate(ANGLE_X_TO_POS_ZERO, false);
		// Set current position as position zero.
		this.armMotorX.resetTachoCount();

		// -- Calibrate motor Y --

		this.armMotorY.setSpeed(CALIBRATION_SPEED);

		// Move back until sensor gets pressed.
		this.armMotorY.backward(false);

		// When the sensor is pressed, stop calibration and bounce back to what
		// will become position zero.
		this.sensorY.fetchSample(this.sensorSampleY, 0);
		while (this.sensorSampleY[0] == 0) {
			this.sensorY.fetchSample(this.sensorSampleY, 0);
		}
		// Brake motor.
		this.armMotorY.stop();
		this.armMotorY.setSpeed(NORMAL_OPERATION_SPEED);
		// Move to final position zero.
		this.armMotorY.rotate(ANGLE_Y_TO_POS_ZERO, false);
		// Set current position as position zero.
		this.armMotorY.resetTachoCount();

		// TODO Z
	}

	/*
	 * publiv void safeRotateTo(int x, int y, int z) { // Convert line to set of
	 * Motor instructions. ArrayList<MotorInstruction> instructions = new
	 * ArrayList<MotorInstruction>(); // TODO: Replace "0" by MotorZ when it's
	 * available. instructions = geometry.getLineInstructions( new
	 * Vector3D(this.armMotorX.getPosition(), this.armMotorY .getPosition(), 0),
	 * new Vector3D(x, y, z));
	 * 
	 * }
	 */

	public void executeInstructions(ArrayList<MotorInstruction> instructions) {
		ArmMotor currentMotor = null;
		for (MotorInstruction instruction : instructions) {
			switch (instruction.action) {
			case MotorInstruction.MOVE_X:
				currentMotor = this.armMotorX;
				break;
			case MotorInstruction.MOVE_Y:
				currentMotor = this.armMotorY;
				break;
			case MotorInstruction.MOVE_Z:
				// TODO:
				// currentMotor = this.motorZ;
				break;
			}

			currentMotor.rotate(instruction.value, false);
		}
	}

	public void armSyncTest() {
		this.armMotorX.synchronizeWithArm(this.armMotorY);

		this.armMotorX.rotate(180, true);
		this.armMotorY.rotate(360, true);

		this.armMotorX.stopSyncWithArmAndRunOperations(false);
	}
}
