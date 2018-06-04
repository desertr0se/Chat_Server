package cs;

import com.google.gson.annotations.Expose;

public class Command {

	@Expose
	private User user;
	@Expose
	private String command;
	@Expose
	private String argument1;
	@Expose
	private String argument2;

	public Command(User u, String c, String a1, String a2) {
		user = u;
		command = c;
		argument1 = a1;
		argument2 = a2;
	}

	public String getCommand() {
		return command;
	}

	public String getArgument1() {
		return argument1;
	}

	public String getArgument2() {
		return argument2;
	}

	public User getUser() {
		return user;
	}
}
