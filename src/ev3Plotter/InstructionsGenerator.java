package ev3Plotter;

import java.util.ArrayList;

import common.IntVector3D;

public class InstructionsGenerator {
	// Default resolution is the finest.
	private int resolution = 1;
	// Default mode is the non-aliased mode.
	private boolean aliasedMode = false;

	/**
	 * Default constructor. Initializes the class in non-aliased mode.
	 */
	public InstructionsGenerator() {
	}

	/**
	 * Constructor with resolution parameter. Implies the aliased mode.
	 *
	 * @param resolution
	 *            Defines how fine the drawing should be. Minimum value: 1
	 *            (finest).
	 */
	public InstructionsGenerator(int resolution) {
		this.resolution = resolution;
		this.aliasedMode = true;
	}

	/**
	 * Constructor with resolution parameter. Implies the aliased mode.
	 *
	 * @param resolution
	 *            Defines how fine the drawing should be. Minimum value: 1
	 *            (finest).
	 */
	public InstructionsGenerator(boolean aliasedMode) {
		this.aliasedMode = aliasedMode;
	}

	/**
	 * Get a list of Motor instructions to draw given line.
	 *
	 * @param start
	 *            Source point of the line to draw.
	 * @param end
	 *            Destination point of the line to draw.
	 * @return List of instructions to draw the line.
	 */
	public ArrayList<MotorInstruction> getLineInstructions(IntVector3D start,
			IntVector3D end) {
		if (this.aliasedMode) {
			return this.getAliasedLineInstructions(start, end);
		} else {
			return this.getDirectLineInstructions(start, end);
		}
	}

	protected ArrayList<MotorInstruction> getAliasedLineInstructions(
			IntVector3D start, IntVector3D end) {
		// Array of instructions that will be returned.
		ArrayList<MotorInstruction> instructions = new ArrayList<MotorInstruction>();

		// Compute signed distances.
		IntVector3D distance = new IntVector3D(end.x - start.x,
				end.y - start.y, end.z - start.z);

		// Find out what the biggest distance is: That will be used as the
		// number of steps to go through.
		int numberSteps = Math.max(Math.abs(distance.x), Math.abs(distance.y));
		numberSteps = Math.max(numberSteps, Math.abs(distance.z));

		// Declare all variables that will be used within the process loop.
		IntVector3D previousPos = new IntVector3D(start.x, start.y, start.z);
		IntVector3D currentPos = new IntVector3D(start.x, start.y, start.z);

		float theoreticalX = 0;
		float theoreticalY = 0;
		float theoreticalZ = 0;

		// Compute list of Motor instructions to draw given line.
		// Note that i starts at 1 intentionally.
		for (int i = 1; i <= numberSteps; i++) {
			theoreticalX = start.x + distance.x * (i / (float) numberSteps);
			theoreticalY = start.y + distance.y * (i / (float) numberSteps);
			theoreticalZ = start.z + distance.z * (i / (float) numberSteps);

			currentPos.x = Math.round(theoreticalX);
			currentPos.y = Math.round(theoreticalY);
			currentPos.z = Math.round(theoreticalZ);

			// Compute needed moves to handle current step.
			this.handleAliasedStep(instructions, previousPos, currentPos,
					this.resolution);
		}

		// Execute one last time the step handling function with a resolution of
		// 1, to apply any pending move to reach destination point.
		this.handleAliasedStep(instructions, previousPos, currentPos, 1);

		// Return the generated set of instructions.
		return instructions;
	}

	protected ArrayList<MotorInstruction> getDirectLineInstructions(
			IntVector3D start, IntVector3D end) {
		// Array of instructions that will be returned.
		ArrayList<MotorInstruction> instructions = new ArrayList<MotorInstruction>();

		// Compute the corresponding relative move.
		IntVector3D distance = new IntVector3D(end.x - start.x,
				end.y - start.y, end.z - start.z);

		// Find out what the biggest distance is: That will be used as the
		// reference for speed ratios.
		int maxDistance = Math.max(Math.abs(distance.x), Math.abs(distance.y));
		maxDistance = Math.max(maxDistance, Math.abs(distance.z));

		// If max distance turn out to be zero, it means there's no movement
		// need. Return an empty set of instructions.
		if (maxDistance == 0) {
			return instructions;
		}

		// Prepare speed ratios.
		float speedRatioX = distance.x / maxDistance;
		float speedRatioY = distance.y / maxDistance;
		float speedRatioZ = distance.z / maxDistance;

		MotorInstruction instruction = new MotorInstruction(end.x, end.y,
				end.z, speedRatioX, speedRatioY, speedRatioZ);

		// Add the only needed instruction.
		instructions.add(instruction);

		return instructions;
	}

	/**
	 * Computes moves X, Y and Z for given step.
	 *
	 * @param instructions
	 *            Current list of instructions. Important: Gets updated by this
	 *            function.
	 * @param previousPos
	 *            Previous point's position. Important: Gets updated by this
	 *            function.
	 * @param currentPos
	 *            Current point's position.
	 * @param resolution
	 *            Resolution to apply to this move. Should be >= 1 (all values <
	 *            1 will be treated as 1). This represents the minimum number of
	 *            steps that each move should travel. For instance
	 */
	protected void handleAliasedStep(ArrayList<MotorInstruction> instructions,
			IntVector3D previousPos, IntVector3D currentPos, int resolution) {

		// Compute what move should be done if resolution was 1.
		IntVector3D move = new IntVector3D(currentPos.x - previousPos.x,
				currentPos.y - previousPos.y, currentPos.z - previousPos.z);

		MotorInstruction lastestInstruction = new MotorInstruction();

		// Handle X axis and by taking resolution into account.
		if (Math.abs(move.x) >= resolution) {
			if (instructions.size() > 0) {
				lastestInstruction = instructions.get(instructions.size() - 1);
			}
			if (MotorInstruction.MOVE_X == lastestInstruction.getMoveType()) {
				lastestInstruction.moveX += move.x;
			} else {
				instructions.add(new MotorInstruction(currentPos.x, 0, 0));
			}

			previousPos.x = currentPos.x;
		}

		// Handle Y axis and by taking resolution into account.
		if (Math.abs(move.y) >= resolution) {
			if (instructions.size() > 0) {
				lastestInstruction = instructions.get(instructions.size() - 1);
			}
			if (MotorInstruction.MOVE_Y == lastestInstruction.getMoveType()) {
				lastestInstruction.moveY += move.y;
			} else {
				instructions.add(new MotorInstruction(0, currentPos.y, 0));
			}

			previousPos.y = currentPos.y;
		}

		// Handle Z axis and by taking resolution into account.
		if (Math.abs(move.z) >= resolution) {
			if (instructions.size() > 0) {
				lastestInstruction = instructions.get(instructions.size() - 1);
			}
			if (MotorInstruction.MOVE_Z == lastestInstruction.getMoveType()) {
				lastestInstruction.moveZ += move.z;
			} else {
				instructions.add(new MotorInstruction(0, 0, currentPos.z));
			}

			previousPos.y = currentPos.y;
		}
	}

	/**
	 * Returns a list of MotorInstructions from given list of degree positions.
	 *
	 * @param positions
	 *            List of positions, in degrees.
	 * @param factorizeList
	 *            If set to TRUE, all unnecessary points will be removed from
	 *            the list of positions.
	 * @return List of MotorInstructions ready to be run by an ArmSystem.
	 */
	public ArrayList<MotorInstruction> getInstructions(
			ArrayList<IntVector3D> positions, boolean factorizeList) {
		// Array of instructions that will be returned.
		ArrayList<MotorInstruction> instructions = new ArrayList<MotorInstruction>();

		// No positions? Return an empty set of instructions.
		if (positions.size() == 0) {
			return instructions;
		}

		if (factorizeList) {
			positions = this.factorizePositionList(positions);
		}

		// Start position.
		IntVector3D previousPos = positions.get(0);

		// Loop over points to get line instructions that connect them.
		// Note: i starts intentionally at 1.
		for (int i = 1; i < positions.size(); i++) {
			IntVector3D currentPos = positions.get(i);

			instructions.addAll(this.getLineInstructions(previousPos,
					currentPos));

			previousPos = currentPos;
		}

		return instructions;
	}

	public ArrayList<IntVector3D> factorizePositionList(
			ArrayList<IntVector3D> positions) {
		long ax, ay, az, bx, by, bz, cx, cy, cz;
		long ux, uy, uz, vx, vy, vz;
		ArrayList<Double> ratios = new ArrayList<Double>();
		boolean skippablePosition = false;
		int j;
		double refRatio;

		ArrayList<IntVector3D> out = new ArrayList<IntVector3D>();

		// Loop over points to remove all unnecessary ones.
		// Note: i starts intentionally at 2 (3rd pos).
		out.add(positions.get(0));
		for (int i = 2; i < positions.size(); i++) {
			ax = positions.get(i - 2).x;
			ay = positions.get(i - 2).y;
			az = positions.get(i - 2).z;
			bx = positions.get(i - 1).x;
			by = positions.get(i - 1).y;
			bz = positions.get(i - 1).z;
			cx = positions.get(i).x;
			cy = positions.get(i).y;
			cz = positions.get(i).z;
			ratios.clear();
			skippablePosition = true;

			ux = bx - ax;
			uy = by - ay;
			uz = bz - az;
			vx = cx - bx;
			vy = cy - by;
			vz = cz - bz;

			// Handle all special cases where we know for sure there's no
			// possible simplification.
			if (ux != 0 && vx == 0 || uy != 0 && vy == 0 || uz != 0 && vz == 0
					|| ux == 0 && vx != 0 || uy == 0 && vy != 0 || uz == 0
					&& vz != 0) {
				skippablePosition = false;
			} else {
				if (vx != 0) {
					ratios.add((double) ux / (double) vx);
				}

				if (vy != 0) {
					ratios.add((double) uy / (double) vy);
				}

				if (vz != 0) {
					ratios.add((double) uz / (double) vz);
				}

				if (ratios.size() != 0) {
					refRatio = ratios.get(0);

					if (refRatio > 0) {
						// i starts at 1 as first element already stored in
						// refRatio.
						for (j = 1; j < ratios.size(); j++) {
							if (ratios.get(j) != refRatio) {
								skippablePosition = false;
								break;
							}
						}
					} else {
						// When ratio is negative, it means that vector V is
						// facing
						// the opposite direction. In this case, do not skip any
						// position.
						skippablePosition = false;
					}
				}
			}

			// Add all not skippable points.
			if (!skippablePosition) {
				out.add(positions.get(i - 1));
			}
		}

		out.add(positions.get(positions.size() - 1));

		return out;
	}
}
