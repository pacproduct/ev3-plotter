package ev3Manager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Manager {

	public static void main(String[] args) {
		Socket clientSocket;
		try {
			clientSocket = new Socket("10.0.1.1", 7777);

			try (OutputStreamWriter out = new OutputStreamWriter(
					clientSocket.getOutputStream(), StandardCharsets.UTF_8)) {
				out.write("Bonjour\n");
				out.flush();

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
				}

				out.write("exit\n");
				out.flush();

			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
