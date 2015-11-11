package ev3Plotter;

import java.util.ArrayList;

import common.FloatVector3D;

public class Geometry {

	/**
	 * Get a list of Motor instructions to draw a circle on the horizontal plan.
	 *
	 * @param circleCenter
	 *            Center coordinates of the circle to draw (millimeters).
	 * @param radius
	 *            Radius of the circle to draw (millimeters).
	 * @param startAngle
	 *            Starting angle, in degrees. Draws the circle anti-clockwise.
	 *            If set to 0, will start from the right side. If set to 90,
	 *            will start from the bottom. And so on.
	 * @param numPoints
	 *            Number of points the circle should be made of (i.e. setting
	 *            this to 5 would give a pentagon...).
	 * @return List of instructions to draw the horizontal circle.
	 */
	static public ArrayList<FloatVector3D> getHorizontalCircleInstructions(
			FloatVector3D circleCenter, float radius, float startAngle,
			int numPoints) {
		// Array of instructions that will be returned.
		ArrayList<FloatVector3D> positions = new ArrayList<FloatVector3D>();

		// Convert start angle to radians.
		startAngle = (float) ((startAngle * Math.PI) / 180);

		// Initial values.
		double angle = startAngle;
		float startX = (float) (circleCenter.x + radius * Math.cos(angle));
		float startY = (float) (circleCenter.y + radius * Math.sin(angle));

		// Start position.
		positions.add(new FloatVector3D(startX, startY, circleCenter.z));

		// Loop over points to get line positions that connect them.
		// Note: i starts intentionally at 1, as the initial position has
		// already been added to the list.
		for (int i = 1; i <= numPoints; i++) {
			angle = startAngle + (2 * Math.PI * i) / numPoints;
			float currentX = (float) (circleCenter.x + radius * Math.cos(angle));
			float currentY = (float) (circleCenter.y + radius * Math.sin(angle));

			positions
					.add(new FloatVector3D(currentX, currentY, circleCenter.z));
		}

		return positions;
	}
}
