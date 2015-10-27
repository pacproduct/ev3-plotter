package ev3Plotter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

import common.NetCom;
import common.NetComPacket;

public class Plotter {
	// Global resolution to be applied to drawings/movements.
	public static final int MOVEMENT_RESOLUTION = 5;

	// Constants.
	// Scale converter reference, in millimeters.
	public static final float SCALE_REF_MILLIMETERS = 100.0f;
	// Scale converter reference, in degrees.
	public static final int SCALE_REF_DEGREES = 720;
	// Port accepting clients.
	public static final int LISTENING_PORT = 3000;
	// Timeout
	public static final int WAITING_FOR_CLIENT_PACKET_TIMEOUT = 3600 * 1000;

	protected static ArmSystem as = null;
	protected static InstructionsGenerator ig = null;
	protected static ScaleConverter sc = null;

	public static void main(String[] args) {
		NetCom netCom;
		Plotter.displayText("Initializing...");

		// Instantiate components.
		Plotter.as = new ArmSystem();
		Plotter.ig = new InstructionsGenerator(false);
		Plotter.sc = new ScaleConverter(Plotter.SCALE_REF_MILLIMETERS,
				Plotter.SCALE_REF_DEGREES);

		// Calibrate arms.
		// as.calibrate();

		// Wait for a client.
		Plotter.displayText("Waiting 4 client...");
		try {
			netCom = Plotter.waitForClient(LISTENING_PORT);

			// Fire.
			Plotter.displayText("Initializing...");
			Plotter.handleClient(netCom);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		// // DEBUG TEST.
		// // 1. Receive an array of millimeters positions.
		// ArrayList<FloatVector3D> millimetersPositions = new
		// ArrayList<FloatVector3D>();
		// millimetersPositions.add(new FloatVector3D(0, 0, 0));
		// millimetersPositions.add(new FloatVector3D(50, 0, 0));
		// millimetersPositions.add(new FloatVector3D(100, 50, 0));
		// millimetersPositions.add(new FloatVector3D(50, 100, 0));
		// millimetersPositions.add(new FloatVector3D(0, 50, 0));
		// millimetersPositions.add(new FloatVector3D(50, 0, 0));
		// // 2. Convert it to degrees positions.
		// ArrayList<IntVector3D> degreesPositions = Plotter.sc
		// .millimetersToDegrees(millimetersPositions);
		// // 3. Generate a list of MotorInstructions from it.
		// ArrayList<MotorInstruction> instructions = Plotter.ig
		// .getInstructions(degreesPositions);
		// // 4. Fire in the hole.
		// Plotter.as.executeInstructions(instructions);
		// // END OF DEBUG.
	}

	protected static NetCom waitForClient(int port) throws IOException {
		ServerSocket serverSocket;
		Socket clientSocket;

		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		serverSocket.close();

		return new NetCom(clientSocket);
	}

	protected static void handleClient(NetCom netCom) throws IOException {
		NetComPacket packet = null;

		Boolean exitFlag = false;
		while (!exitFlag) {
			packet = netCom.receivePacket(WAITING_FOR_CLIENT_PACKET_TIMEOUT);

			if (NetComPacket.TYPE_EXIT == packet.type) {
				exitFlag = true;
			}
		}
	}

	protected static void displayText(String text) {
		LCD.clear();
		LCD.drawString(text, 0, 0);
	}
}
