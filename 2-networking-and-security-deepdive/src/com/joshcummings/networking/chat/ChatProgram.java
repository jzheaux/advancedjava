package com.joshcummings.networking.chat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatProgram {
	private static final Map<String, Queue<ChatMessage>> messages = new HashMap<String, Queue<ChatMessage>>() {
		 public Queue<ChatMessage> get(Object key) {
			 Queue<ChatMessage> q  = super.get(key);
			 if ( q == null ) {
				 q = new LinkedList<>();
				 this.put((String)key, q);
			 }
			 return q;
		 };
	};

	private static final ExecutorService es = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		try (ServerSocket ss = new ServerSocket(5050)) {
			while (true) {
				Socket s = ss.accept();

				es.submit(() -> {
					try (ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());){
						String name = (String) ois.readObject();
						MessageReader reader = new MessageReader(ois);
						MessageWriter writer = new MessageWriter(name, oos);
						Thread t1 = new Thread(reader);
						Thread t2 = new Thread(writer);
						t1.start();
						t2.start();
						t1.join();
						t2.join();
					} catch (Exception e) {
						e.printStackTrace();
					}

				});
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			es.shutdown();
		}
	}

	private static class MessageReader implements Runnable {
		private ObjectInputStream ois;
		
		public MessageReader(ObjectInputStream ois) {
			this.ois = ois;
		}
		
		public void run() {
			while ( true ) {
				try {
					ChatMessage cm = (ChatMessage)ois.readObject();
					for ( String to : cm.getTo() ) {
						synchronized ( messages.get(to) ) {
							messages.get(to).offer(cm);
							messages.get(to).notify();
						}
					}
				} catch (Exception e) {
					System.out.println("Received bad request: " + e.getMessage());
				}
			}
		}
	}
	
	private static class MessageWriter implements Runnable {
		private String name;
		private ObjectOutputStream oos;

		public MessageWriter(String name, ObjectOutputStream oos) {
			this.name = name;
			this.oos = oos;
		}

		@Override
		public void run() {
			while (true) {
				ChatMessage toSend;
				synchronized ( messages.get(name) ) {
					while (messages.get(name).isEmpty()) {
						try {
							messages.get(name).wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					toSend = messages.get(name).poll();
				}
				
				try {
					oos.writeObject(toSend);
					oos.flush();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}

}
