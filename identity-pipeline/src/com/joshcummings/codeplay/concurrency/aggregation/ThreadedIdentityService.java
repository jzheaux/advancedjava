package com.joshcummings.codeplay.concurrency.aggregation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.Person;

public class ThreadedIdentityService implements IdentityService {
	private volatile Queue<Identity> verifiedIdentities = new ConcurrentLinkedQueue<>();
	
	private ExecutorService es = Executors.newWorkStealingPool();
	
	@Override
	public boolean persistOrUpdateBestMatch(Identity identity) {
		PriorityQueue<MergeCandidate> candidates = new PriorityQueue<>();
		
		// find candidates
		for (Identity i : verifiedIdentities) {
			int score = 0;
			if ( i.getEmailAddress() != null && i.getEmailAddress().equals(identity.getEmailAddress()) ) {
				score += 50;
			}
			if ( i.getPhoneNumber() != null && i.getPhoneNumber().equals(identity.getPhoneNumber()) ) {
				score += 15;
			}
			if ( i.getName().equals(identity.getName()) ) {
				score += 35;
			}
			if ( score >= 50 ) {
				candidates.offer(new MergeCandidate(i, score));
			}
		}
		
		// pick the best one and lock on it
		for ( MergeCandidate candidate : candidates ) {
			Person id = (Person)candidate.getCandidate();
			// optimistic merging; we'll suppose that if we can't get the lock, we can merge at some other point
			if ( id.getLock().tryLock() ) {
				try {
					if ( id.getEmailAddress() == null ) {
						id.setEmailAddress(identity.getEmailAddress());
					}
					if ( id.getPhoneNumber() == null ) {
						id.setPhoneNumber(identity.getPhoneNumber());
					}
					id.addAddresses(identity.getAddresses());
					return true;
				} catch ( Exception e ) {
					// rollback, out of scope
				} finally {
					id.getLock().unlock();
				}
			}
		}	
		
		verifiedIdentities.add(identity);
		
		return false;
	}

	public Identity getOne(Predicate<Identity> p) {
		BlockingQueue<Identity> identities = new LinkedBlockingQueue<Identity>(verifiedIdentities);
		int size = identities.size();
		BlockingQueue<Identity> result = new LinkedBlockingQueue<>();
		
		int segments = Math.min((int)Math.ceil(size / 8d), 8);
		while ( !identities.isEmpty() ) {
			Queue<Identity> segment = new LinkedList<>();
			identities.drainTo(segment, (int)Math.ceil(size / (segments*1.0)));
			es.submit(() -> {
				while ( !segment.isEmpty() && result.isEmpty() ) {
					Identity candidate = segment.poll();
					if ( p.test(candidate) )  {
						result.add(candidate);
					}
				}
			});
		}
		
		// Again, there are several threads running concurrently; however, since we only
		// care about a single result, we only need to wait for the shared object to have a
		// single value to proceed.
		try {
			return result.take();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null; // ???
		}
	}
	
	@Override
	public List<Identity> search(Predicate<Identity> pred) {
		// While we won't be blocking, BlockingQueue has a nice method for extracting a set number
		// of elements into a secondary queue.
		BlockingQueue<Identity> identities = new LinkedBlockingQueue<Identity>(verifiedIdentities);
		int size = identities.size();
		
		// In this case, we will populate a shared object that is thread-safe.
		Queue<Identity> result = new ConcurrentLinkedQueue<>();
		
		int segments = Math.min((int)Math.ceil(size / 8d), 8);
		CountDownLatch cdl = new CountDownLatch(segments);
		while ( !identities.isEmpty() ) {
			Queue<Identity> segment = new LinkedList<>();
			identities.drainTo(segment, (int)Math.ceil(size / (segments*1.0)));
			
			// This time, we'll use CountDownLatch instead of making a list of Futures and blocking on them.
			// On the one hand, we have to pre-calculate the number of threads we will use. On the other, 
			// this requires less machinery to ensure that the reliant code (below) waits for all
			// searches to complete.
			es.submit(() -> {
				while ( !segment.isEmpty() ) {
					Identity candidate = segment.poll();
					if ( pred.test(candidate) )  {
						result.add(candidate);
					}
				}
				cdl.countDown();
			});
		}
		
		try {
			cdl.await();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		
		return new ArrayList<>(result);
	}
}
