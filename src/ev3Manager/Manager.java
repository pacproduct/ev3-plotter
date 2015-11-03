package ev3Manager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import common.NetCom;
import common.NetComPacket;

public class Manager {

	public static void main(String[] args) {
		// Check parameters.
		if (args.length < 2) {
			System.err
					.println("This program is waiting for 2 parameters: remoteHost command");
			System.exit(1);
		}

		// Get parameters
		String remoteHost = args[0];
		String commandToSend = args[1];

		NetCom clientSocket;
		try {
			clientSocket = new NetCom(new Socket(remoteHost, 7777));

			NetComPacket packetToSend = NetCom
					.parseRawStringPacket(commandToSend);

			clientSocket.sendPacket(packetToSend);

			Thread.sleep(5000);

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
	}
}
