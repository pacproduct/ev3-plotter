package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NetCom {
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;

	public NetCom(Socket socket) throws IOException {
		this.socket = socket;

		this.in = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream(), StandardCharsets.UTF_8));
		this.out = new BufferedWriter(new OutputStreamWriter(
				this.socket.getOutputStream(), StandardCharsets.UTF_8));
	}

	/**
	 * Receives a packet.
	 *
	 * @param timeoutMs
	 *            Timeout, in milliseconds. Set to 0 for infinite wait.
	 * @return The read packet.
	 * @throws IOException
	 */
	public NetComPacket receivePacket(int timeoutMs) throws IOException {
		NetComPacket returnPacket = new NetComPacket(NetComPacket.TYPE_NULL,
				null);

		this.socket.setSoTimeout(timeoutMs);
		String receivedLine = this.in.readLine();

		// If receivedLine is null, return null packet.
		if (receivedLine == null) {
			return returnPacket;
		}

		// Parse received line.
		String packetParts[] = null;
		packetParts = receivedLine.split(":", 2);

		// The packet needs to have at least 1 part. Else, return null packet.
		if (packetParts.length < 1) {
			return returnPacket;
		}

		// Extract packet type.
		int packetType = NetComPacket.TYPE_NULL;
		try {
			packetType = Integer.parseInt(packetParts[0]);
		} catch (NumberFormatException e) {
		}

		switch (packetType) {
		case NetComPacket.TYPE_SET_SPEED:
			// Make sure there are 2 parts in the received packet.
			if (packetParts.length == 2) {
				try {
					Integer speed = Integer.parseInt(packetParts[1]);
					returnPacket = new NetComPacket(packetType, speed);
				} catch (NumberFormatException e) {
				}
			}
			break;

		case NetComPacket.TYPE_DISPLAY_TEXT:
			// Make sure there are 2 parts in the received packet.
			if (packetParts.length == 2) {
				returnPacket = new NetComPacket(packetType, packetParts[1]);
			}
			break;

		case NetComPacket.TYPE_EXIT:
			returnPacket = new NetComPacket(packetType, 0);
			break;

		default:
			// Nothing to do here, the returned packet will be the null one
			// initialized at the beginning of this method.
			break;
		}

		// TODO
		return returnPacket;
	}

	/**
	 * Sends a packet.
	 *
	 * @param packet
	 *            The packet to send.
	 * @throws IOException
	 */
	public void sendPacket(NetComPacket packet) throws IOException {
		String stringToSend = "";

		// Extract packet type.
		String packetType = String.valueOf(packet.type);
		switch (packet.type) {
		case NetComPacket.TYPE_SET_SPEED:
			stringToSend = packetType + ":" + packet.integerContent.toString();
			break;

		case NetComPacket.TYPE_DISPLAY_TEXT:
			stringToSend = packetType + ":" + packet.stringContent;
			break;

		default:
			// Nothing to do here, the string to send will be the empty one
			// initialized at the beginning of this method.
			break;
		}

		stringToSend += "\n";

		this.out.write(stringToSend);
		this.out.flush();
	}
}
