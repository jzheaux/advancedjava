package com.joshcummings.badvisibility;
public class LoopMayNeverEnd { 
  boolean done = false; 

  /**
   * Because this is operating in a different thread, it may not see the change to the
   * done variable. Threads do not always see the change made by another thread to a shared
   * variable.
   */
  void work() { 
    while (!done) { 
      // do work 
    } 
  } 
 
  /**
   * What could possibly go wrong? :)
   */
  void stopWork() { 
    done = true; 
  } 
  
  public static void main(String[] args) throws InterruptedException {
	LoopMayNeverEnd l = new LoopMayNeverEnd();
	Thread t = new Thread(() -> l.work());
	t.start();
	
	/**
	 * We give the JVM some time to choose to optimize the while loop.
	 */
	System.out.println("Sleeping...");
	Thread.sleep(1000);
	System.out.println("Done Sleeping...");
	
	/**
	 * Even though we set done to false, work may not see the change
	 */
	l.stopWork();
  }
} 