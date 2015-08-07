package plotter;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;

public class ArmMotor {
	private BaseRegulatedMotor[] allMotors;
	private BaseRegulatedMotor[] slaveMotors;

	private int directionOperator = 1;

	private boolean hasAMaximumPosition = false;
	private boolean hasAMinimumPosition = false;
	private int maximumPosition = 0;
	private int minimumPosition = 0;

	/**
	 * Constructor.
	 *
	 * @param ports
	 *            Ports this virtual motor is connected to. I.e. : Several
	 *            physical motors can be simultaneously commanded by this class.
	 * @param forward
	 *            Indicates whereas this motor should behave normally. Set it to
	 *            true to behave the normal way, or false to behave in reverse
	 *            mode.
	 * @param bigMotor
	 *            Indicates whether this motor will be an
	 *            EV3LargeRegulatedMotor, or a medium one. Set to true if using
	 *            a large one.
	 */
	public ArmMotor(Port[] ports, boolean forward, boolean bigMotor) {
		if (forward) {
			this.directionOperator = 1;
		} else {
			this.directionOperator = -1;
		}

		// Initialize array of motors.
		this.allMotors = new BaseRegulatedMotor[ports.length];

		if (bigMotor) {
			for (int i = 0; i < ports.length; i++) {
				this.allMotors[i] = new EV3LargeRegulatedMotor(ports[i]);
			}
		} else {
			for (int i = 0; i < ports.length; i++) {
				this.allMotors[i] = new EV3MediumRegulatedMotor(ports[i]);
			}
		}

		// Prepare list of slave motors.
		this.slaveMotors = new BaseRegulatedMotor[ports.length - 1];
		for (int i = 1; i < ports.length; i++) {
			this.slaveMotors[i - 1] = this.allMotors[i];
		}
	}

	public void setMaximumPosition(int val) {
		this.hasAMaximumPosition = true;
		this.maximumPosition = val;
	}

	public void setMinimumPosition(int val) {
		this.hasAMinimumPosition = true;
		this.minimumPosition = val;
	}

	public void rotate(int val, boolean immediateReturn) {
		int currentPosition = this.getPosition();

		this.rotateTo(currentPosition + val, immediateReturn);
	}

	public int getPosition() {
		// Consider motor number 0 as the master.
		return (int) this.allMotors[0].getPosition() * this.directionOperator;
	}

	/*
	 * Synchronized functions, abstracting access to physical motors.
	 */

	public void rotateTo(int val, boolean immediateReturn) {
		// Make sure value is within boundaries.
		if (this.hasAMaximumPosition && val > this.maximumPosition) {
			val = this.maximumPosition;
		}
		if (this.hasAMinimumPosition && val < this.minimumPosition) {
			val = this.minimumPosition;
		}

		// Prepare value to rotate to.
		int destValue = val * this.directionOperator;

		// Apply action, in a synchronized fashion.
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.length; i++) {
			this.allMotors[i].rotateTo(destValue, true);
		}
		this.stopSyncAndLaunchSynchronizedOperations(immediateReturn);
	}

	public void forward(boolean immediateReturn) {
		if (this.hasAMaximumPosition) {
			this.rotateTo(this.maximumPosition, immediateReturn);
		} else {
			if (this.directionOperator == 1) {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.length; i++) {
					this.allMotors[i].forward();
				}
				this.stopSyncAndLaunchSynchronizedOperations(true);
			} else {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.length; i++) {
					this.allMotors[i].backward();
				}
				this.stopSyncAndLaunchSynchronizedOperations(true);
			}
		}
	}

	public void backward(boolean immediateReturn) {
		if (this.hasAMinimumPosition) {
			this.rotateTo(this.minimumPosition, immediateReturn);
		} else {
			if (this.directionOperator == 1) {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.length; i++) {
					this.allMotors[i].backward();
				}
				this.stopSyncAndLaunchSynchronizedOperations(true);
			} else {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.length; i++) {
					this.allMotors[i].forward();
				}
				this.stopSyncAndLaunchSynchronizedOperations(true);
			}
		}
	}

	public void resetTachoCount() {
		for (int i = 0; i < allMotors.length; i++) {
			this.allMotors[i].resetTachoCount();
		}
	}

	public void setSpeed(int val) {
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.length; i++) {
			this.allMotors[i].setSpeed(val);
		}
		this.stopSyncAndLaunchSynchronizedOperations(true);
	}

	public void stop() {
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.length; i++) {
			this.allMotors[i].stop(true);
		}
		this.stopSyncAndLaunchSynchronizedOperations(true);
	}

	/*
	 * Protected utility functions.
	 */

	protected void synchronizeAllMotors() {
		this.allMotors[0].synchronizeWith(this.slaveMotors);

		// Build the array of other motors.synchronizeWith(this.motors);

		// TODO : When function do synchronize Arms between them is done, take
		// the other Arm's motors into account in the list.
	}

	protected void stopSyncAndLaunchSynchronizedOperations(
			boolean immediateReturn) {
		this.allMotors[0].endSynchronization();

		if (!immediateReturn) {
			for (int i = 0; i < allMotors.length; i++) {
				this.allMotors[i].waitComplete();
			}
		}
	}
}
