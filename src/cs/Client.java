package cs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Client {

	private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
	public static final String SERVER_HOSTNAME = "localhost";
	public static final int SERVER_PORT = 5051;
	private static boolean disconnected;
	private static Socket serverSocket;
	private static DataInputStream clientDataInputStream;
	private static DataOutputStream clientDataOutputStream;
	private static User user;
	private static Command command;

	public static void main(String args[]) {

		Scanner scn = new Scanner(System.in);

		connectToServer(scn);

		// establish the connection
		try {
			serverSocket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("You are now connected.");

		try {
		// obtaining input and out streams
		clientDataInputStream = new DataInputStream(serverSocket.getInputStream());
		clientDataOutputStream = new DataOutputStream(serverSocket.getOutputStream());

		loginOrRegisterClient(scn);

		System.out.println("To see available commands type list-commands");

		Thread rensponseThread = obtainingResponseFromServer();
		rensponseThread.start();

		sendInstructionsToServer(scn);
		rensponseThread.stop();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				clientDataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				clientDataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void loginOrRegisterClient(Scanner scn) {
		while (true) {
			System.out.println("You can register or login. For registration type: register>username>password."
					+ " For login type login>username>password, with correct username and possward.");

			String inputInstructions = scn.nextLine();
			String instructions[] = inputInstructions.split(">");
			if (instructions.length < 3) {
				System.out.println("Invalid command or arguments. Please try again");
			} else {
				user = new User(instructions[1], clientDataInputStream, clientDataOutputStream);
				command = new Command(user, instructions[0], instructions[1], instructions[2]);
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				String string = gson.toJson(command, Command.class);
				writeToServer(clientDataOutputStream, string);
				try {
					String message = clientDataInputStream.readUTF();
					System.out.println(message);
					if (message.equals("You successfully logged in.")) {
						disconnected = false;
						break;
					}
				} catch (IOException e) {
					LOGGER.log(Level.CONFIG, e.toString(), e);
					e.printStackTrace();
				}
			}
		}
	}

	private static Thread obtainingResponseFromServer() {
		Thread responseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String message = clientDataInputStream.readUTF();
						System.out.println(message);
						if (message.equals("You successfully disconnect.")) {
							disconnected = true;
							System.out.println("Click /Enter/ to close the program.");
							break;

						}
					} catch (IOException e) {
						LOGGER.log(Level.CONFIG, e.toString(), e);
						e.printStackTrace();
					}
				}
			}
		});
		return responseThread;
	}

	private static void sendInstructionsToServer(Scanner scn){
		String inputInstructions;
		try {
		  while (!disconnected) {
			inputInstructions = scn.nextLine();
			if (inputInstructions.equals("list-commands")) {
				System.out.println("- disconnect\n" + "- list-users\n" + "- send>username>message\n"
						+ "- send-file>username>file_location\n" + "- create-room>room_name\n"
						+ "- delete-room>room_name\n" + "- join-room>room_name\n" + "- leave-room>room_name\n"
						+ "- list-rooms\n" + "- list-users>room\n" + "- list-users-room>room_name\n"
						+ "- send-room>room_name>message\n" + "- logout\n");
			} else {

				String instructions[] = inputInstructions.split(">");
				if (instructions.length > 3 || instructions.length < 1) {
					System.out.println("Invalid number of arguments.");
				} else if (instructions.length == 1) {
					command = new Command(user, instructions[0], null, null);
				} else if (instructions.length == 2) {
					command = new Command(user, instructions[0], instructions[1], null);
				} else {
					command = new Command(user, instructions[0], instructions[1], instructions[2]);
					if (instructions[0].equals("sedn-file")) {
						File file = new File(instructions[2]);
						try(InputStream is = new FileInputStream(file)){
						  int count;
						  byte[] buffer = new byte[8192];
						  while ((count = is.read(buffer)) > 0) {
							clientDataOutputStream.write(buffer, 0, count);
						  }
					    }
					}
				}
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				String string = gson.toJson(command, Command.class);
				writeToServer(clientDataOutputStream, string);

			}
		 }
	   } catch(IOException e) {
		e.printStackTrace();
	   }
    }

	private static void connectToServer(Scanner scn) {
		String connection;
		do {
			System.out.println("You are curently not connected. To connect to the servet type connect.");
			connection = scn.nextLine();
		} while (!connection.equals("connect"));
	}

	private static void writeToServer(DataOutputStream dos, String string) {
		try {
			dos.writeUTF(string);
		} catch (IOException e) {
			LOGGER.log(Level.CONFIG, e.toString(), e);
			e.printStackTrace();
		}
	}
}
