package plotter;

public class MotorScale {
	private double ratioMillemetersToDegrees = 0;

	/*
	 * - De s'initialiser avec un ratio du type "(float) mm => (int) degrés"
	 * (par ex "100.07" => "231") - De passer des mm aux degrés de rotation -
	 * Inversement - De convertir une liste de Positions en une liste de
	 * AngularPositions (techniquement, c'est un vector)
	 */

	public MotorScale(float millimeters, int degrees) {
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
}
