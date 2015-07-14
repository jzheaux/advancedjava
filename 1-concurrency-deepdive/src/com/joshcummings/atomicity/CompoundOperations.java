package com.joshcummings.atomicity;

/**
 * Is this thread-safe?
 * 
 * @author uradmin
 *
 */
public class CompoundOperations {
	private static class Command {
		private final String processedCommand;

		public Command(String processedCommand) {
			this.processedCommand = processedCommand;
		}
		
		public void doCommand() {
			System.out.println("Doing command... " + processedCommand);
		}
	}
	
	private Command command; 
	
	public void processCommand(String cmd) {
		command = new Command(cmd);
	}
	
	public void runCommand(String whichThread) {
		System.out.println(whichThread + " is running");
		command.doCommand();
	}
	
	public static void main(String[] args) {
		CompoundOperations co = new CompoundOperations();
		Thread one = new Thread(new Runnable() {
			@Override
			public void run() {
				co.processCommand("King Dark e1 -> e2");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				co.runCommand("one");
			}
		});
		
		Thread two = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				co.processCommand("Knight Light c5 -> d3");
				co.runCommand("two");
			}
		});
		
		one.start();
		two.start();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
