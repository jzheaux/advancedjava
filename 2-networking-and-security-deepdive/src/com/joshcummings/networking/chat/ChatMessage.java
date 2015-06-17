package com.joshcummings.networking.chat;
import java.io.Serializable;
import java.util.List;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String from;
	private final List<String> to;
	private final String message;

	public ChatMessage(String from, List<String> to, String message) {
		this.from = from;
		this.to = to;
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public List<String> getTo() {
		return to;
	}

	public String getMessage() {
		return message;
	}
}