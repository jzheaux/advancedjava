package com.joshcummings.networking.chat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


public class ChatClient {
	private static class MessageReader implements Runnable {
		private InputStream is;
		
		public MessageReader(InputStream is) {
			this.is = is;
		}
		
		@Override
		public void run() {
			try ( ObjectInputStream ois = new ObjectInputStream(is)) {
				while ( true ) {
					ChatMessage cm = (ChatMessage)ois.readObject();
					System.out.println(cm.getFrom() + ": " + cm.getMessage());
				}
			} catch ( Exception e ) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	private static class MessageWriter implements Runnable {
		private String name;
		private OutputStream os;
		private static final Scanner INPUT = new Scanner(System.in);
		
		public MessageWriter(String name, OutputStream os) {
			this.name = name;
			this.os = os;
		}
		
		@Override
		public void run() {
			try ( ObjectOutputStream oos = new ObjectOutputStream(os)) {
				oos.writeObject(name);
				oos.flush();
				while ( true ) {
					System.out.print("> ");
					String[] message = INPUT.nextLine().split(":");
					oos.writeObject(new ChatMessage(name, Arrays.asList(message[0]), message[1]));
					oos.flush();
				}
			} catch ( Exception e ) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	public static void main(String[] args) {
		String name = args[0];
		try  {
			Socket s = new Socket("localhost", 5050); 
			new Thread(new MessageWriter(name, s.getOutputStream())).start();
			new Thread(new MessageReader(s.getInputStream())).start();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
