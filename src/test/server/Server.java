package test.server;

import java.util.ArrayList;

import common.NetComPacket;

import ev3Plotter.FloatVector3D;
import ev3Plotter.InstructionsGenerator;
import ev3Plotter.IntVector3D;
import ev3Plotter.MotorInstruction;
import ev3Plotter.ScaleConverter;

public class Server {

	public static void main(String[] args) throws InterruptedException {

		// ServerSocket serverSocket;
		// Socket clientSocket;
		//
		// int timeout = 0;
		//
		// try {
		// serverSocket = new ServerSocket(7777);
		// clientSocket = serverSocket.accept();
		//
		// NetCom netCom = new NetCom(clientSocket);
		//
		// NetComPacket packet = null;
		//
		// Boolean exitFlag = false;
		// while (!exitFlag) {
		// packet = netCom.receivePacket(timeout);
		//
		// if (NetComPacket.TYPE_EXIT == packet.type) {
		// exitFlag = true;
		// }
		//
		// Server.displayPacketData(packet);
		// }
		//
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// DEBUG TEST.
		// 1. Receive an array of millimeters positions.
		ArrayList<FloatVector3D> millimetersPositions = new ArrayList<FloatVector3D>();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(10, 10, 10));
			millimetersPositions.add(new FloatVector3D(5, 5, 5));
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(50, 0, 0));
			millimetersPositions.add(new FloatVector3D(100, 50, 0));
			millimetersPositions.add(new FloatVector3D(50, 100, 0));
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(0, 0, 20));
			millimetersPositions.add(new FloatVector3D(0, 0, 10));
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(0, 0, 0));
			millimetersPositions.add(new FloatVector3D(25, 75, 0));
			millimetersPositions.add(new FloatVector3D(0, 50, 0));
			millimetersPositions.add(new FloatVector3D(50, 0, 0));
			millimetersPositions.add(new FloatVector3D(50, 0, 50));
			millimetersPositions.add(new FloatVector3D(50, 0, 50));
			millimetersPositions.add(new FloatVector3D(50, 0, 0));
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("List generation: Elapsed time: " + elapsedTime
				+ "ms");
		startTime = System.currentTimeMillis();

		// 2. Convert it to degrees positions.
		ScaleConverter sc = new ScaleConverter(100, 100);
		InstructionsGenerator ig = new InstructionsGenerator();
		ArrayList<IntVector3D> degreesPositions = sc
				.millimetersToDegrees(millimetersPositions);

		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println("Milimeters2Degrees: Elapsed time: " + elapsedTime
				+ "ms");
		startTime = System.currentTimeMillis();

		// 3. Generate a list of MotorInstructions from it.
		ArrayList<MotorInstruction> instructions = ig.getInstructions(
				degreesPositions, true);

		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out
				.println("Simplified instruction list generation: Elapsed time: "
						+ elapsedTime + "ms");
		startTime = System.currentTimeMillis();

		// 4. Fire in the hole.
		// for (MotorInstruction mi : instructions) {
		// System.out.println(mi);
		// }
		// END OF DEBUG.

	}

	private static void displayPacketData(NetComPacket packet) {

		switch (packet.type) {
		case NetComPacket.TYPE_SET_SPEED:
			System.out.println("type  : " + "speed");
			System.out.println("value : " + packet.integerContent.toString());
			break;

		case NetComPacket.TYPE_DISPLAY_TEXT:
			System.out.println("type  : " + "text");
			System.out.println("value : " + packet.stringContent);
			break;

		case NetComPacket.TYPE_EXIT:
			System.out.println("type  : " + "exit");
			break;

		default:
			// Nothing to do here, the string to send will be the empty one
			// initialized at the beginning of this method.
			break;
		}
		System.out.println("---------------");
	}
}
