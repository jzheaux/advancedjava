package com.joshcummings.codeplay.concurrency.throttle;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import com.joshcummings.codeplay.concurrency.Address;
import com.joshcummings.codeplay.concurrency.AddressVerifier;

public class ConnectionLimitingAddressVerifier implements AddressVerifier {
	private Semaphore limiter = new Semaphore(5);
	private AddressVerifier delegate;
	
	public ConnectionLimitingAddressVerifier(AddressVerifier delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void verify(List<Address> address) {
		try {
			// only five threads can acquire this lock at a time, ensuring that the
			// underlying address verified doesn't get overloaded.
			limiter.acquire();
			delegate.verify(address);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		} finally {
			// not putting this in a finally block is ASKING for deadlock!
			limiter.release();
		}
	}

	private static class ConfigurableSemaphore {
		private ReentrantLock inTransition;
		private Semaphore active;
		/*private Semaphore inactive;
		
		private Condition transitionComplete = inTransition.newCondition();
		
		private int permits;*/
		
		public ConfigurableSemaphore(int permits) {
			setPermits(permits);
		}
		
		public void setPermits(int permits) {
			inTransition.lock();
			try {
				active = new Semaphore(permits);
			} finally {
				inTransition.unlock();
			}
		}
		
		public Semaphore getSemaphore() {
			inTransition.lock();
			try {
				return active;
			} finally {
				inTransition.unlock();
			}
		}
		
		/*
		public void updatePermitLimit(int permits) {
			inTransition.lock();
			try {
				inactive = active;
				active = new Semaphore(permits);
			} finally {
				inTransition.unlock();
			}
		}
		
		public void acquire() throws InterruptedException {
			inTransition.lock();
			try {
				if ( inactive != null ) {
					transitionComplete.await();
				}
				active.acquire();
			} finally {
				inTransition.unlock();
			}
		}
		
		public void release() {
			inTransition.lock();
			try {
				if ( inactive != null ) {
					inactive.release();
					if ( inactive.availablePermits() == permits ) {
						this.permits = active.availablePermits();
						inactive = null;
						transitionComplete.signalAll();
					}
				} else {
					active.release();
				}
			} finally {
				inTransition.unlock();
			}
		}*/
	}
}
