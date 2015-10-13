package plotter;

import java.util.ArrayList;

public class ScaleConverter {
	private double ratioMillemetersToDegrees = 0;

	/*
	 * - De passer des mm aux degrés de rotation - Inversement - De convertir
	 * une liste de Positions en une liste de AngularPositions (techniquement,
	 * c'est un vector)
	 */

	public ScaleConverter(float millimeters, int degrees) {
		this.ratioMillemetersToDegrees = ((double) degrees)
				/ ((double) millimeters);
	}

	public int millimetersToDegrees(float millimeters) {
		return (int) Math.round(((double) millimeters)
				* this.ratioMillemetersToDegrees);
	}

	public float degreesToMillimeters(int degrees) {
		return (float) (((double) degrees) * this.ratioMillemetersToDegrees);
	}

	public IntVector3D millimetersToDegrees(FloatVector3D inputVector) {
		return new IntVector3D(this.millimetersToDegrees(inputVector.x),
				this.millimetersToDegrees(inputVector.y),
				this.millimetersToDegrees(inputVector.z));
	}

	public FloatVector3D degreesToMillimeters(IntVector3D inputVector) {
		return new FloatVector3D(this.degreesToMillimeters(inputVector.x),
				this.degreesToMillimeters(inputVector.y),
				this.degreesToMillimeters(inputVector.z));
	}

	public ArrayList<IntVector3D> millimetersToDegrees(
			ArrayList<FloatVector3D> inputList) {
		ArrayList<IntVector3D> outputList = new ArrayList<IntVector3D>();

		for (FloatVector3D vector : inputList) {
			outputList.add(this.millimetersToDegrees(vector));
		}

		return outputList;
	}

	public ArrayList<FloatVector3D> degreesToMillimeters(
			ArrayList<IntVector3D> inputList) {
		ArrayList<FloatVector3D> outputList = new ArrayList<FloatVector3D>();

		for (IntVector3D vector : inputList) {
			outputList.add(this.degreesToMillimeters(vector));
		}

		return outputList;
	}
}
