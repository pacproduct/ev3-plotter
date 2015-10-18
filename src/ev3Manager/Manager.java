package ev3Manager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import common.NetCom;
import common.NetComPacket;

public class Manager {

	public static void main(String[] args) {
		NetCom clientSocket;
		try {
			clientSocket = new NetCom(new Socket("192.168.1.3", 7777));

			clientSocket.sendPacket(new NetComPacket(
					NetComPacket.TYPE_SET_SPEED, 137));

			clientSocket.sendPacket(new NetComPacket(
					NetComPacket.TYPE_DISPLAY_TEXT, "Poxerfuel!"));

			clientSocket.sendPacket(new NetComPacket(NetComPacket.TYPE_EXIT));

			clientSocket.close();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
