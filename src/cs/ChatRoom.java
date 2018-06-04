package cs;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class ChatRoom {

	private static final String SEPARATOR = ",";

	@Expose
	private User admin;
	@Expose
	private String name;

	private Set<User> activeUsers;

	public ChatRoom(User a, String n, Set<User> au) {
		admin = a;
		name = n;
		activeUsers = au;
	}

	public boolean isChatRoomActive() {
		return activeUsers.size() > 0;
	}
	
	public String getActiveUsers() {
		return activeUsers.stream()
			.map(user -> user.toString())
			.collect(Collectors.joining(SEPARATOR));
	}

	public void setActiveUsers(Set<User> au) {
		activeUsers = au;
	}

	public void addUser(User u) {
		activeUsers.add(u);
	}

	public boolean containsUser(User u) {
		return activeUsers.contains(u);
	}

	public void removeUser(User u) {
		activeUsers.remove(u);
	}

	public boolean isAdmin(User u) {
		return admin.equals(u);
	}

	public Set<User> getUsers(){
		return activeUsers;
	}
}
