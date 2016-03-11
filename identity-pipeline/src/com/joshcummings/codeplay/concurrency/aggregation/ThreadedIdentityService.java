package com.joshcummings.codeplay.concurrency.aggregation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.Person;

public class ThreadedIdentityService implements IdentityService {
	private volatile Queue<Identity> verifiedIdentities = new ConcurrentLinkedQueue<>();
	
	private ExecutorService es = Executors.newWorkStealingPool();
	
	@Override
	public boolean persistOrUpdateBestMatch(Identity identity) {
		class BestHolder { MergeCandidate best; }
		BestHolder is = new BestHolder();
		
		for ( Identity i : verifiedIdentities ) {
			if ( i.getLock().tryLock() ) {
				try {
					scoreMatch(identity, i).ifPresent(mergeable -> {
						if ( is.best == null || mergeable.getScore() > is.best.getScore() ) {
							is.best.getCandidate().getLock().unlock();
							is.best = mergeable;
							i.getLock().lock(); // get a second lock for the same thread
						}
					});
				} catch ( Exception e ) {
					// don't really eat!
				} finally {
					i.getLock().unlock();
				}
			}
		}	
		
		if ( is.best != null ) {
			try {
				Person candidate = (Person)is.best.getCandidate();
				merge((Person)identity, candidate);
				return true;
			} finally {
				is.best.getCandidate().getLock().unlock();
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
		return verifiedIdentities.parallelStream().filter(pred).collect(Collectors.toList());
	}

	private Optional<MergeCandidate> scoreMatch(Identity incoming, Identity existing) {
		int score = 0;
		if ( existing.getEmailAddress() != null &&
				existing.getEmailAddress().equals(incoming.getEmailAddress()) ) {
			score += 50;
		}
		if ( existing.getPhoneNumber() != null && 
				existing.getPhoneNumber().equals(incoming.getPhoneNumber()) ) {
			score += 15;
		}
		if ( existing.getName().equals(incoming.getName()) ) {
			score += 35;
		}
		if ( score >= 50 ) {
			return Optional.of(new MergeCandidate(existing, score));
		}
		return Optional.empty();
	}
	
	private void merge(Person incoming, Person existing) {
		if ( existing.getEmailAddress() == null ) {
			existing.setEmailAddress(incoming.getEmailAddress());
		}
		if ( existing.getPhoneNumber() == null ) {
			existing.setPhoneNumber(incoming.getPhoneNumber());
		}
		existing.addAddresses(incoming.getAddresses());
	}
}
