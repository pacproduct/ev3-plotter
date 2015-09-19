package plotter;

import java.util.ArrayList;

public class GeometryInstructions {
	// Default resolution is the finest.
	private int resolution = 1;

	/**
	 * Default constructor. Does nothing special.
	 */
	public GeometryInstructions() {
	}

	/**
	 * Constructor with resolution parameter.
	 *
	 * @param resolution
	 *            Defines how fine the drawing should be. Minimum value: 1
	 *            (finest).
	 */
	public GeometryInstructions(int resolution) {
		this.resolution = resolution;
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
	public ArrayList<MotorInstruction> getLineInstructions(Vector3D start,
			Vector3D end) {
		// Array of instructions that will be returned.
		ArrayList<MotorInstruction> instructions = new ArrayList<MotorInstruction>();

		// Compute signed distances.
		Vector3D distance = new Vector3D(end.x - start.x, end.y - start.y,
				end.z - start.z);

		// Find out what the biggest distance is: That will be used as the
		// number of steps to go through.
		int numberSteps = Math.max(Math.abs(distance.x), Math.abs(distance.y));
		numberSteps = Math.max(numberSteps, Math.abs(distance.z));

		// Declare all variables that will be used within the process loop.
		Vector3D previousPos = new Vector3D(start.x, start.y, start.z);
		Vector3D currentPos = new Vector3D(start.x, start.y, start.z);

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
			Vector3D previousPos, Vector3D currentPos, int resolution) {

		// Compute what move should be done if resolution was 1.
		Vector3D move = new Vector3D(currentPos.x - previousPos.x, currentPos.y
				- previousPos.y, currentPos.z - previousPos.z);

		MotorInstruction lastestInstruction = new MotorInstruction();

		// Handle X axis and by taking resolution into account.
		if (Math.abs(move.x) >= resolution) {
			if (instructions.size() > 0) {
				lastestInstruction = instructions.get(instructions.size() - 1);
			}
			if (MotorInstruction.MOVE_X == lastestInstruction.getMoveType()) {
				lastestInstruction.moveX += move.x;
			} else {
				instructions.add(new MotorInstruction(move.x, 0, 0));
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
				instructions.add(new MotorInstruction(0, move.y, 0));
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
				instructions.add(new MotorInstruction(0, 0, move.z));
			}

			previousPos.y = currentPos.y;
		}
	}

	/**
	 * Get a list of Motor instructions to draw a circle on the horizontal plan.
	 *
	 * @param circleCenter
	 *            Center coordinates of the circle to draw.
	 * @param radius
	 *            Radius of the circle to draw.
	 * @param startAngle
	 *            Starting angle, in degrees. Draws the circle anti-clockwise.
	 *            If set to 0, will start from the right side. If set to 90,
	 *            will start from the bottom. And so on.
	 * @return List of instructions to draw the horizontal circle.
	 */
	public ArrayList<MotorInstruction> getHorizontalCircleInstructions(
			Vector3D circleCenter, int radius, double startAngle, int numPoints) {
		// Array of instructions that will be returned.
		ArrayList<MotorInstruction> instructions = new ArrayList<MotorInstruction>();

		// Convert start angle to radians.
		startAngle = (startAngle * Math.PI) / 180;

		// Initial values.
		double angle = startAngle;
		int previousX = Math.round((float) (circleCenter.x + radius
				* Math.cos(angle)));
		int previousY = Math.round((float) (circleCenter.y + radius
				* Math.sin(angle)));

		// Loop over points to get line instructions that connect them.
		// Note: i starts intentionally at 1.
		for (int i = 1; i <= numPoints; i++) {
			angle = startAngle + (2 * Math.PI * i) / numPoints;
			int currentX = Math.round((float) (circleCenter.x + radius
					* Math.cos(angle)));
			int currentY = Math.round((float) (circleCenter.y + radius
					* Math.sin(angle)));

			instructions.addAll(this.getLineInstructions(new Vector3D(
					previousX, previousY, circleCenter.z), new Vector3D(
					currentX, currentY, circleCenter.z)));

			previousX = currentX;
			previousY = currentY;
		}

		return instructions;
	}
}
