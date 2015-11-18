package com.joshcummings.codeplay.concurrency.aggregation;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.Person;

public class ThreadedIdentityService implements IdentityService {
	private List<Identity> verifiedIdentities = new ArrayList<>();
	
	private ExecutorService es = Executors.newCachedThreadPool();
	
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

	@Override
	public List<Identity> search(Predicate<Identity> pred) {
		List<Identity> filtered = new ArrayList<>();
		int min = Math.min(verifiedIdentities.size(), 8);
		CountDownLatch cdl = new CountDownLatch(min);
		for ( int i = 0; i < min; i++ ) {
			int startIndex = i*verifiedIdentities.size() / min;
			int endIndex = startIndex + verifiedIdentities.size() / min;
			es.submit(() -> {
				int j = 0;
				for ( Identity id: verifiedIdentities ) {
					if ( j >= startIndex && j < endIndex && pred.test(id) ) {
						synchronized ( filtered ) {
							filtered.add(id);
						}
						cdl.countDown();
					}
					j++;
				}
			});
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return filtered;
	}

}
