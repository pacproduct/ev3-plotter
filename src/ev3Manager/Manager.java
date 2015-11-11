package ev3Manager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import common.FloatVector3D;
import common.NetCom;
import common.NetComPacket;

public class Manager {

	public static void main(String[] args) {
		// Check parameters.
		// if (args.length < 2) {
		// System.err
		// .println("This program is waiting for 2 parameters: remoteHost command");
		// System.exit(1);
		// }

		// Get parameters
		String remoteHost = args[0];
		String commandToSend = args[1];

		NetCom clientSocket;
		try {
			clientSocket = new NetCom(new Socket(remoteHost, 7777));

			// List of positions.
			ArrayList<FloatVector3D> pos = new ArrayList<FloatVector3D>();

			// Create packet with circle.
			pos.add(new FloatVector3D(0, 0, 0));
			// pos.addAll(Geometry.getHorizontalCircleInstructions(
			// new FloatVector3D(50, 50, 0), 50, 270, 16));

			pos.add(new FloatVector3D(100, 0, 0));
			pos.add(new FloatVector3D(100, 80, 0));
			pos.add(new FloatVector3D(0, 80, 0));
			pos.add(new FloatVector3D(0, 0, 0));
			pos.add(new FloatVector3D(100, 0, 0));
			pos.add(new FloatVector3D(0, 80, 0));
			pos.add(new FloatVector3D(100, 80, 0));

			pos.add(new FloatVector3D(0, 0, 0));
			NetComPacket packet = new NetComPacket(
					NetComPacket.TYPE_STACK_MILLIMETER_POSITIONS, pos);

			clientSocket.sendPacket(packet);

			clientSocket.sendPacket(new NetComPacket(
					NetComPacket.TYPE_RUN_PENDING_ACTIONS));

			Thread.sleep(2000);

			clientSocket.sendPacket(new NetComPacket(NetComPacket.TYPE_EXIT));

			Thread.sleep(1000);

			clientSocket.close();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----

		// Check parameters.
		// if (args.length < 2) {
		// System.err
		// .println("This program is waiting for 2 parameters: remoteHost command");
		// System.exit(1);
		// }
		//
		// // Get parameters
		// String remoteHost = args[0];
		// String commandToSend = args[1];
		//
		// NetCom clientSocket;
		// try {
		// clientSocket = new NetCom(new Socket(remoteHost, 7777));
		//
		// NetComPacket packetToSend = NetCom
		// .parseRawStringPacket(commandToSend);
		//
		// clientSocket.sendPacket(packetToSend);
		//
		// clientSocket.sendPacket(new NetComPacket(
		// NetComPacket.TYPE_RUN_PENDING_ACTIONS));
		//
		// Thread.sleep(2000);
		//
		// clientSocket.sendPacket(new NetComPacket(NetComPacket.TYPE_EXIT));
		//
		// Thread.sleep(1000);
		//
		// clientSocket.close();
		// } catch (UnknownHostException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
