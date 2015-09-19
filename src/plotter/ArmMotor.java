package plotter;

import java.util.ArrayList;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;

public class ArmMotor {
	// Lists of all the motors this ArmMotor is made of.
	private ArrayList<BaseRegulatedMotor> allMotors = new ArrayList<BaseRegulatedMotor>();
	// List of all motors, minus the master one.
	private ArrayList<BaseRegulatedMotor> slaveMotors = new ArrayList<BaseRegulatedMotor>();

	private int directionOperator = 1;

	// -- Max and min position variables --
	// Boolean indicating whether max and min positions are active or not.
	private boolean hasAMaximumPosition = false;
	private boolean hasAMinimumPosition = false;
	// Max and min position.
	private int maximumPosition = 0;
	private int minimumPosition = 0;

	// -- Arm synchronization variables --
	// This turns to true when an Arm sync has been started.
	private boolean armSyncIsOn = false;
	// Complete list of synchronized slave motors when syncing 2 arms.
	private ArrayList<BaseRegulatedMotor> crossArmSynchronizedSlaveMotors = new ArrayList<BaseRegulatedMotor>();

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
	 * @param useBigMotors
	 *            Indicates whether this Arm uses EV3LargeRegulatedMotor motors,
	 *            or medium ones. Set to true if it is using large ones.
	 */
	public ArmMotor(Port[] ports, boolean forward, boolean useBigMotors) {
		if (forward) {
			this.directionOperator = 1;
		} else {
			this.directionOperator = -1;
		}

		if (useBigMotors) {
			for (int i = 0; i < ports.length; i++) {
				this.allMotors.add(new EV3LargeRegulatedMotor(ports[i]));
			}
		} else {
			for (int i = 0; i < ports.length; i++) {
				this.allMotors.add(new EV3MediumRegulatedMotor(ports[i]));
			}
		}

		// Prepare list of slave motors.
		for (int i = 1; i < ports.length; i++) {
			this.slaveMotors.add(this.allMotors.get(i));
		}
	}

	/**
	 * Set a maximum angle for this AmrMotor. When set, asking the ArMotor to
	 * rotate after this angle won't be possible anymore.
	 *
	 * @param angle
	 *            Angle, in degrees.
	 */
	public void setMaximumPosition(int angle) {
		this.hasAMaximumPosition = true;
		this.maximumPosition = angle;
	}

	/**
	 * Set a minimum angle for this AmrMotor. When set, asking the ArMotor to
	 * rotate before this angle won't be possible anymore.
	 *
	 * @param angle
	 *            Angle, in degrees.
	 */
	public void setMinimumPosition(int angle) {
		this.hasAMinimumPosition = true;
		this.minimumPosition = angle;
	}

	/**
	 * Rotates this ArmMotor by the given angle.
	 *
	 * @param angle
	 *            Angle, in degrees.
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete.
	 */
	public void rotate(int angle, boolean immediateReturn) {
		int currentPosition = this.getPosition();

		this.rotateTo(currentPosition + angle, immediateReturn);
	}

	/**
	 * Returns ArmMotor's current position (angle).
	 *
	 * @return The current angle of this ArmMotor, in degrees.
	 */
	public int getPosition() {
		// Consider motor number 0 as the master.
		return (int) this.allMotors.get(0).getPosition()
				* this.directionOperator;
	}

	/*
	 * Synchronized functions, abstracting access to physical motors.
	 */

	/**
	 * Rotates this ArmMotor by the given angle.
	 *
	 * @param angle
	 *            Angle, in degrees.
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete.
	 */
	public void rotateTo(int angle, boolean immediateReturn) {
		// Make sure value is within boundaries.
		if (this.hasAMaximumPosition && angle > this.maximumPosition) {
			angle = this.maximumPosition;
		}
		if (this.hasAMinimumPosition && angle < this.minimumPosition) {
			angle = this.minimumPosition;
		}

		// Prepare value to rotate to.
		int destValue = angle * this.directionOperator;

		// Apply action, in a synchronized fashion.
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.size(); i++) {
			this.allMotors.get(i).rotateTo(destValue, true);
		}
		this.stopSyncAndRunOperations(immediateReturn);
	}

	/**
	 * Rotates this ArmMotor forward.
	 *
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete. Note that if no maximum position was
	 *            set prior to calling this function, this parameter won't have
	 *            any effect and the function will return immediately.
	 */
	public void forward(boolean immediateReturn) {
		if (this.hasAMaximumPosition) {
			this.rotateTo(this.maximumPosition, immediateReturn);
		} else {
			if (this.directionOperator == 1) {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.size(); i++) {
					this.allMotors.get(i).forward();
				}
				this.stopSyncAndRunOperations(true);
			} else {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.size(); i++) {
					this.allMotors.get(i).backward();
				}
				this.stopSyncAndRunOperations(true);
			}
		}
	}

	/**
	 * Rotates this ArmMotor backward.
	 *
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete. Note that if no maximum position was
	 *            set prior to calling this function, this parameter won't have
	 *            any effect and the function will return immediately.
	 */
	public void backward(boolean immediateReturn) {
		if (this.hasAMinimumPosition) {
			this.rotateTo(this.minimumPosition, immediateReturn);
		} else {
			if (this.directionOperator == 1) {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.size(); i++) {
					this.allMotors.get(i).backward();
				}
				this.stopSyncAndRunOperations(true);
			} else {
				this.synchronizeAllMotors();
				for (int i = 0; i < allMotors.size(); i++) {
					this.allMotors.get(i).forward();
				}
				this.stopSyncAndRunOperations(true);
			}
		}
	}

	/**
	 * Resets the tachometer of this ArmMotor.
	 */
	public void resetTachoCount() {
		for (int i = 0; i < allMotors.size(); i++) {
			this.allMotors.get(i).resetTachoCount();
		}
	}

	/**
	 * Sets the operating speed of this ArmMotor.
	 *
	 * @param speed
	 *            Speed, in number of degrees per seconds.
	 */
	public void setSpeed(int speed) {
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.size(); i++) {
			this.allMotors.get(i).setSpeed(speed);
		}
		this.stopSyncAndRunOperations(true);
	}

	/**
	 * Causes this ArmMotor to stop, pretty much instantaneously by resisting
	 * any further motion.
	 */
	public void stop() {
		this.synchronizeAllMotors();
		for (int i = 0; i < allMotors.size(); i++) {
			this.allMotors.get(i).stop(true);
		}
		this.stopSyncAndRunOperations(true);
	}

	/*
	 * Arm synchronization
	 */

	/**
	 * Returns the list of all motors this ArmMotor is made of, the master motor
	 * included.
	 *
	 * @return List of all the motors this ArmMotor is made of.
	 */
	public ArrayList<BaseRegulatedMotor> getAllMotors() {
		return this.allMotors;
	}

	/**
	 * Starts a synchronized operation with another ArmMotor.
	 *
	 * @param otherArms
	 *            The other ArmMotor this ArmMotor should synchronize with.
	 */
	public void synchronizeWithArm(ArmMotor[] otherArms) {
		// Remember we're currently in the middle of a cross-Arm sync.
		this.armSyncIsOn = true;

		// Compute the list of all the slave motors to synchronize with.
		this.crossArmSynchronizedSlaveMotors = this.slaveMotors;
		for (int i = 0; i < otherArms.length; i++) {
			this.crossArmSynchronizedSlaveMotors.addAll(otherArms[i]
					.getAllMotors());
		}

		// Sync all motors with master one.
		this.allMotors
				.get(0)
				.synchronizeWith(
						this.crossArmSynchronizedSlaveMotors
								.toArray(new BaseRegulatedMotor[this.crossArmSynchronizedSlaveMotors
										.size()]));
	}

	/**
	 * Stops the synchronization process with another ArmMotor, and run all the
	 * pending operations in a synchronized fashion.
	 *
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete.
	 */
	public void stopSyncWithArmAndRunOperations(boolean immediateReturn) {
		// Run operations.
		this.allMotors.get(0).endSynchronization();

		// Wait for operations to complete, if needed.
		if (!immediateReturn) {
			for (int i = 0; i < this.crossArmSynchronizedSlaveMotors.size(); i++) {
				this.crossArmSynchronizedSlaveMotors.get(i).waitComplete();
			}
		}

		// Disable synchronization.
		this.armSyncIsOn = false;
		this.crossArmSynchronizedSlaveMotors.clear();
	}

	/*
	 * Protected utility functions.
	 */

	/**
	 * Starts a synchronized operation between all local motors of this
	 * ArmMotor.
	 */
	protected void synchronizeAllMotors() {
		// Act only when a global sync between Arms is not in progress.
		if (!this.armSyncIsOn) {
			this.allMotors.get(0).synchronizeWith(
					this.slaveMotors
							.toArray(new BaseRegulatedMotor[this.slaveMotors
									.size()]));
		}
	}

	/**
	 * Stops the synchronization process between all local motors of this
	 * ArmMotor.
	 *
	 * @param immediateReturn
	 *            If true, method returns immediately. If false, waits for the
	 *            operation to complete.
	 */
	protected void stopSyncAndRunOperations(boolean immediateReturn) {
		// Act only when a global sync between Arms is not in progress.
		if (!this.armSyncIsOn) {
			this.allMotors.get(0).endSynchronization();

			if (!immediateReturn) {
				for (int i = 0; i < allMotors.size(); i++) {
					this.allMotors.get(i).waitComplete();
				}
			}
		}
	}
}
