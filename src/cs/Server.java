package cs;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {

	private static File userInformation = new File("userInfo.txt");
	private static Map<String, User> activeUsers = new ConcurrentHashMap<String, User>();
	private static Map<String, String> allUsers = new ConcurrentHashMap<String, String>();
	private static Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<String, ChatRoom>();
	private static ServerSocket serverSocket;
	private static final int port = 5051;

	public static void main(String[] args) {
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadUsers();

		clientAdding();
	}

	private static void clientAdding() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client request received : " + clientSocket);

				ClientHandler clientHandler = new ClientHandler(clientSocket, activeUsers, allUsers, chatRooms);
				Thread clientThread = new Thread(clientHandler);
				System.out.println("Adding this client to active client list");

				clientThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadUsers() {
		if (userInformation.exists()) {
			try {
				userInformation.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (Stream<String> stream = Files.lines(Paths.get(userInformation.toURI()))) {
			stream.forEach(x -> {
				String y[] = x.split("#");
				allUsers.put(y[0], y[1]);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getUserInfo() {
		return userInformation;
	}
}
