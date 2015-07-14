package com.joshcummings.badordering;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BadlyOrdered {
  boolean a = false;
  boolean b = false;

  /**
   * Re-ordering is less-commonly done by the JVM, however, it is LEGAL for the JVM
   * to reorder the two lines in this method at runtime. What would happen to threadTwo()
   * if that were the case?
   */
  void threadOne() {
    a = true;
    b = true;
  }

  boolean threadTwo() {
    boolean r1 = b; // sees true
    boolean r2 = a; // sees false
    boolean r3 = a; // sees true
    return (r1 && !r2) && r3; // returns true
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
	  ExecutorService es = Executors.newCachedThreadPool();
	  for ( int i = 0; i < 100000; i++ ) {
		  BadlyOrdered bo = new BadlyOrdered();
		  Future<?> f = es.submit(() -> bo.threadOne());
		  boolean result = bo.threadTwo();
		  if ( result ) System.out.println("Re-ordering!");
		  f.get();
	  }
	  System.out.println("Done!");
  }
}