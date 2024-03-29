package minicraft.network;

import minicraft.core.Game;
import minicraft.item.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public abstract class MinicraftConnection extends Thread implements MinicraftProtocol {

	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;

	protected MinicraftConnection(String threadName, @Nullable Socket socket) {
		super(threadName);
		this.socket = socket;

		if (socket == null) return;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException ex) {
			System.err.println("Failed to initialize i/o streams for socket:");
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			System.err.println("CONNECTION ERROR: null socket, cannot initialize i/o streams...");
			ex.printStackTrace();
		}
	}

	public void run() {
		if (Game.debug) System.out.println("Starting " + this);

		StringBuilder currentData = new StringBuilder();

		while(isConnected()) {
			int read = -2;

			try {
				read = in.read();
			} catch (IOException ex) {
				System.err.println(this + " had a problem reading its input stream (will continue trying): " + ex.getMessage());
				ex.printStackTrace();
			}

			if (read < 0) {
				if (Game.debug) System.out.println(this + " reached end of input stream.");
				break;
			}

			if (read > 0) { // if it is valid character that is not the null character, then add it to the string.
				currentData.append( (char)read );
			}
			else if (currentData.length() > 0) { // read MUST equal 0 at this point, aka a null character; the if statement makes it ignore sequential null characters.

				InputType inType = MinicraftProtocol.getInputType(currentData.charAt(0));

				if (inType == null)
					System.err.println("SERVER: invalid packet received; input type is not valid.");
				else
					parsePacket(inType, currentData.substring(1));

				currentData = new StringBuilder();
			}
		}

		if (Game.debug) System.out.println("Run loop ended for " + this + "; ending connection.");

		endConnection();
	}

	protected int getConnectedPort() {
		return socket.getPort();
	}

	protected abstract boolean parsePacket(InputType inType, String data);

	protected void sendData(InputType inType, String data) {
		if (socket == null) return;

		if (Game.packet_debug && Game.isConnectedClient()) System.out.println("Sent:" + inType.toString() + ", " + data);

		char inTypeChar = (char) (inType.ordinal() + 1);
		if (data.contains("\0")) System.err.println("WARNING from " + this + ": data to send contains a null character. Not sending data.");
		else {
			out.print(inTypeChar + data + '\0');
			out.flush();
		}
	}

	@NotNull
	public static String stringToInts(String str, int maxLength) {
		int[] chars = new int[Math.min(str.length(), maxLength)];

		for (int i = 0; i < chars.length; i++)
			chars[i] = (int) str.charAt(i);

		return Arrays.toString(chars);
	}

	// there are a couple methods that are identical in both a server thread, and the client, so I'll just put them here.

	public void sendNotification(String note, int notetime) {
		sendData(InputType.NOTIFY, notetime+ ";" + note);
	}

	public void sendPotionEffect(PotionType type, boolean addEffect) {
		sendData(InputType.POTION, addEffect+ ";" + type.key);
	}

	public void endConnection() {
		if (socket != null && !socket.isClosed()) {
			if (Game.debug) System.out.println("Closing socket and ending connection for: " + this);

			sendData(InputType.DISCONNECT, "");

			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}

	public boolean isConnected() {
		return socket != null && !socket.isClosed() && socket.isConnected();
	}
}
