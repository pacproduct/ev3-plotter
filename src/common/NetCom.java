package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
	 * @throws Exception
	 */
	public NetComPacket receivePacket(int timeoutMs) throws Exception {
		this.socket.setSoTimeout(timeoutMs);
		String receivedLine = this.in.readLine();

		return NetCom.parseRawStringPacket(receivedLine);
	}

	public static NetComPacket parseRawStringPacket(String rawStringPacket)
			throws Exception {
		NetComPacket returnPacket = new NetComPacket(NetComPacket.TYPE_NULL);

		// If receivedLine is null, return null packet.
		if (rawStringPacket == null) {
			return returnPacket;
		}

		// Parse received line.
		String packetParts[] = null;
		packetParts = rawStringPacket.split(":", 2);

		// The packet needs to have at least 1 part. Else, return null packet.
		if (packetParts.length < 1) {
			return returnPacket;
		}

		// Extract packet type.
		int packetType = NetComPacket.TYPE_NULL;
		try {
			packetType = Integer.parseInt(packetParts[0]);
		} catch (NumberFormatException e) {
			throw new Exception("Invalid packet content: " + rawStringPacket);
		}

		switch (packetType) {
		case NetComPacket.TYPE_SET_SPEED:
			// Make sure there are 2 parts in the received packet.
			if (packetParts.length == 2) {
				try {
					Integer speed = Integer.parseInt(packetParts[1]);
					returnPacket = new NetComPacket(packetType, speed);
				} catch (NumberFormatException e) {
					throw new Exception("Invalid packet content: "
							+ rawStringPacket);
				}
			} else {
				throw new Exception("Invalid packet content: "
						+ rawStringPacket);
			}
			break;

		case NetComPacket.TYPE_DISPLAY_TEXT:
			// Make sure there are 2 parts in the received packet.
			if (packetParts.length == 2) {
				returnPacket = new NetComPacket(packetType, packetParts[1]);
			} else {
				throw new Exception("Invalid packet content: "
						+ rawStringPacket);
			}
			break;

		case NetComPacket.TYPE_STACK_MILLIMETER_POSITIONS:
			// Make sure there are 2 parts in the received packet.
			if (packetParts.length == 2) {
				// Parse positions.
				ArrayList<FloatVector3D> positions = new ArrayList<FloatVector3D>();
				String[] positionStrings;
				positionStrings = packetParts[1].split(";");
				float x, y, z;

				String[] coordinates;
				// Convert all position strings to vectors.
				for (String positionString : positionStrings) {
					coordinates = positionString.split(",");

					if (coordinates.length == 3) {
						try {
							x = Float.parseFloat(coordinates[0]);
							y = Float.parseFloat(coordinates[1]);
							z = Float.parseFloat(coordinates[2]);
						} catch (NumberFormatException e) {
							throw new Exception("Invalid packet content: "
									+ rawStringPacket);
						}

						positions.add(new FloatVector3D(x, y, z));
					} else {
						throw new Exception("Invalid packet content: "
								+ rawStringPacket);
					}
				}

				returnPacket = new NetComPacket(packetType, positions);
			} else {
				throw new Exception("Invalid packet content: "
						+ rawStringPacket);
			}
			break;

		case NetComPacket.TYPE_RUN_PENDING_ACTIONS:
		case NetComPacket.TYPE_EXIT:
			returnPacket = new NetComPacket(packetType);
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
		StringBuilder stringToSend = new StringBuilder();

		// Extract packet type.
		String packetType = String.valueOf(packet.type);
		switch (packet.type) {
		case NetComPacket.TYPE_SET_SPEED:
			stringToSend.append(packetType + ":"
					+ packet.integerValue.toString());
			break;

		case NetComPacket.TYPE_DISPLAY_TEXT:
			stringToSend.append(packetType + ":" + packet.stringValue);
			break;

		case NetComPacket.TYPE_STACK_MILLIMETER_POSITIONS:
			for (FloatVector3D position : packet.floatVector3DList) {
				if (!stringToSend.equals("")) {
					stringToSend.append(";");
				}
				stringToSend.append(position.x + "," + position.y + ","
						+ position.z);
			}
			break;

		case NetComPacket.TYPE_RUN_PENDING_ACTIONS:
		case NetComPacket.TYPE_EXIT:
			stringToSend.append(packetType);
			break;

		default:
			// Unknown or null type packet.
			stringToSend.append(NetComPacket.TYPE_NULL);
			break;
		}

		stringToSend.append("\n");

		this.out.write(stringToSend.toString());
		this.out.flush();
	}

	public void close() throws IOException {
		this.socket.shutdownInput();
		this.socket.shutdownOutput();
		this.socket.close();
	}
}
