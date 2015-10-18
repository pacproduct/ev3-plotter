package test.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.NetCom;
import common.NetComPacket;

public class Server {

	public static void main(String[] args) throws InterruptedException {

		ServerSocket serverSocket;
		Socket clientSocket;

		int timeout = 0;

		try {
			serverSocket = new ServerSocket(7777);
			clientSocket = serverSocket.accept();

			NetCom netCom = new NetCom(clientSocket);

			NetComPacket packet = null;

			Boolean exitFlag = false;
			while (!exitFlag) {
				packet = netCom.receivePacket(timeout);

				if (NetComPacket.TYPE_EXIT == packet.type) {
					exitFlag = true;
				}

				Server.displayPacketData(packet);
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
