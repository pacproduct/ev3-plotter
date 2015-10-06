package plotter;

import java.util.ArrayList;

import lejos.hardware.lcd.LCD;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class ArmSystem {
	// Constants.
	// Boolean set to TRUE if motors should rotate in the normal direction, or
	// FALSE if they should behave conversely.
	public static final boolean MOTOR_X_DIRECTION = false;
	public static final boolean MOTOR_Y_DIRECTION = true;
	public static final boolean MOTOR_Z_DIRECTION = true;
	// Motor speed when calibrating.
	public static final int CALIBRATION_SPEED = 400;
	// Motor speed when operating.
	public static final int NORMAL_OPERATION_SPEED = 200;
	// Angle to rotate motors just after going backward until sensor got
	// pressed, to move them to position zero.
	public static final int ANGLE_X_TO_POS_ZERO = 50;
	public static final int ANGLE_Y_TO_POS_ZERO = 50;
	public static final int ANGLE_Z_TO_POS_ZERO = 50;
	// Minimums/Maximums.
	public static final int MIN_POS_X = -2000;
	public static final int MIN_POS_Y = -2000;
	public static final int MIN_POS_Z = -2000;
	public static final int MAX_POS_X = 2000;
	public static final int MAX_POS_Y = 2000;
	public static final int MAX_POS_Z = 2000;
	// Mechanical plays.
	public static final int MECH_PLAY_X = 2;
	public static final int MECH_PLAY_Y = 2;
	public static final int MECH_PLAY_Z = 0;

	// Robotic parts and properties.
	private ArmMotor armMotorX;
	private ArmMotor armMotorY;
	private ArmMotor armMotorZ;
	private SampleProvider sensorX;
	private SampleProvider sensorY;
	private SampleProvider sensorZ;

	/**
	 * Constructor.
	 */
	public ArmSystem() {
		this.armMotorX = new ArmMotor(new Port[] { MotorPort.A, MotorPort.B },
				MOTOR_X_DIRECTION, true, MECH_PLAY_X);
		this.armMotorY = new ArmMotor(new Port[] { MotorPort.C },
				MOTOR_Y_DIRECTION, false, MECH_PLAY_Y);
		this.armMotorZ = new ArmMotor(new Port[] { MotorPort.D },
				MOTOR_Y_DIRECTION, false, MECH_PLAY_Z);

		this.armMotorX.setSpeed(NORMAL_OPERATION_SPEED);
		this.armMotorY.setSpeed(NORMAL_OPERATION_SPEED);
		this.armMotorZ.setSpeed(NORMAL_OPERATION_SPEED);

		// Set min/max positions.
		this.armMotorX.setMinimumPosition(MIN_POS_X);
		this.armMotorX.setMaximumPosition(MAX_POS_X);

		this.armMotorY.setMinimumPosition(MIN_POS_Y);
		this.armMotorY.setMaximumPosition(MAX_POS_Y);

		this.armMotorZ.setMinimumPosition(MIN_POS_Z);
		this.armMotorZ.setMaximumPosition(MAX_POS_Z);

		this.sensorX = new EV3TouchSensor(SensorPort.S1);
		this.sensorY = sensorX;
		this.sensorZ = sensorX;
	}

	/**
	 * Calibrates arms, and moves them to their zero position.
	 */
	public void calibrate() {
		// Calibrate ArmMotor X.
		calibrateArmMotor(this.armMotorX, this.sensorX, ANGLE_X_TO_POS_ZERO);

		// Calibrate ArmMotor Y.
		calibrateArmMotor(this.armMotorY, this.sensorY, ANGLE_Y_TO_POS_ZERO);

		// Calibrate ArmMotor Z.
		calibrateArmMotor(this.armMotorZ, this.sensorZ, ANGLE_Z_TO_POS_ZERO);
	}

	/**
	 * Calibrates an ArmMotor to set its position "zero".
	 *
	 * @param armMotor
	 *            ArmMotor to calibrate.
	 * @param stopSensor
	 *            Sensor detecting when the arm reached its stop.
	 * @param bounceBackAngle
	 *            Number of degrees the Arm's motor(s) need to rotate forward
	 *            after hitting the stop sensor.
	 */
	protected void calibrateArmMotor(ArmMotor armMotor,
			SampleProvider stopSensor, int bounceBackAngle) {
		float[] sensorSample = new float[stopSensor.sampleSize()];

		armMotor.setSpeed(CALIBRATION_SPEED);

		// Move back until sensor gets pressed.
		armMotor.backward(false);

		// When the sensor is pressed, stop calibration and bounce back to what
		// will become position zero.
		stopSensor.fetchSample(sensorSample, 0);
		while (sensorSample[0] == 0) {
			stopSensor.fetchSample(sensorSample, 0);
		}
		// Brake motor.
		armMotor.stop();
		armMotor.setSpeed(NORMAL_OPERATION_SPEED);
		// Move to final position zero.
		armMotor.rotate(bounceBackAngle, false);
		// Set current position as position zero.
		armMotor.resetTachoCount();
	}

	/*
	 * public void safeRotateTo(int x, int y, int z) { // Convert line to set of
	 * Motor instructions. ArrayList<MotorInstruction> instructions = new
	 * ArrayList<MotorInstruction>(); // TODO: Replace "0" by MotorZ when it's
	 * available. instructions = geometry.getLineInstructions( new
	 * Vector3D(this.armMotorX.getPosition(), this.armMotorY .getPosition(), 0),
	 * new Vector3D(x, y, z));
	 * 
	 * }
	 */

	public void executeInstructions(ArrayList<MotorInstruction> instructions) {
		for (MotorInstruction instruction : instructions) {
			armMotorX.synchronizeWithArms(new ArmMotor[] { this.armMotorY,
					this.armMotorZ });

			this.armMotorX.rotate(instruction.moveX, true);
			this.armMotorY.rotate(instruction.moveY, true);
			this.armMotorZ.rotate(instruction.moveZ, true);

			this.armMotorX.stopSyncWithArmsAndRunOperations(false);
		}
	}

	public void armSyncTest() {
		ArrayList<MotorInstruction> instList = new ArrayList<MotorInstruction>();
		MotorInstruction inst = null;

		LCD.clear();
		LCD.drawString("ARM POS => " + this.armMotorX.getPosition(), 0, 0);
		LCD.drawString("REAL POS => "
				+ this.armMotorX.getAllMotors().get(0).getPosition(), 0, 1);
		Delay.msDelay(1000);

		inst = new MotorInstruction(180, 0, 0);
		instList.clear();
		instList.add(inst);
		this.executeInstructions(instList);
		LCD.clear();
		LCD.drawString("ARM POS => " + this.armMotorX.getPosition(), 0, 0);
		LCD.drawString("REAL POS => "
				+ this.armMotorX.getAllMotors().get(0).getPosition(), 0, 1);
		Delay.msDelay(2000);

		inst = new MotorInstruction(-180, 0, 0);
		instList.clear();
		instList.add(inst);
		this.executeInstructions(instList);
		LCD.clear();
		LCD.drawString("ARM POS => " + this.armMotorX.getPosition(), 0, 0);
		LCD.drawString("REAL POS => "
				+ this.armMotorX.getAllMotors().get(0).getPosition(), 0, 1);
		Delay.msDelay(2000);

		inst = new MotorInstruction(-180, 0, 0);
		instList.clear();
		instList.add(inst);
		this.executeInstructions(instList);
		LCD.clear();
		LCD.drawString("ARM POS => " + this.armMotorX.getPosition(), 0, 0);
		LCD.drawString("REAL POS => "
				+ this.armMotorX.getAllMotors().get(0).getPosition(), 0, 1);
		Delay.msDelay(2000);

		inst = new MotorInstruction(180, 0, 0);
		instList.clear();
		instList.add(inst);
		this.executeInstructions(instList);
		LCD.clear();
		LCD.drawString("ARM POS => " + this.armMotorX.getPosition(), 0, 0);
		LCD.drawString("REAL POS => "
				+ this.armMotorX.getAllMotors().get(0).getPosition(), 0, 1);
		Delay.msDelay(2000);

		// EV3LargeRegulatedMotor motorX = (EV3LargeRegulatedMotor)
		// this.armMotorX
		// .getAllMotors().get(0);
		// EV3LargeRegulatedMotor motorY = (EV3LargeRegulatedMotor)
		// this.armMotorX
		// .getAllMotors().get(1);
		//
		// this.syncTest(motorX, motorY);
		//
		// motorX.setSpeed(50);
		// motorY.setSpeed(50);
		// motorX.rotateTo(180, true);
		// motorY.rotateTo(180, false);
		//
		// this.stopSyncTest(motorX, motorY);

		// EV3LargeRegulatedMotor motorX = (EV3LargeRegulatedMotor)
		// this.armMotorX
		// .getAllMotors().get(0);
		// EV3LargeRegulatedMotor motorY = (EV3LargeRegulatedMotor)
		// this.armMotorX
		// .getAllMotors().get(1);
		//
		// motorX.setSpeed(50);
		// motorY.setSpeed(50);
		// motorX.forward();
		// motorY.forward();
		//
		// this.syncTest(motorX, motorY);
		//
		// motorX.setSpeed(100);
		// motorY.setSpeed(100);
		//
		// this.stopSyncTest(motorX, motorY);

		// this.armMotorX.rotate(180, false);
	}
}
