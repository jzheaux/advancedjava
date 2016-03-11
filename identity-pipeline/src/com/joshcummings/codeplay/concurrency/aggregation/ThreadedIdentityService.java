package com.joshcummings.codeplay.concurrency.aggregation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.Person;

public class ThreadedIdentityService implements IdentityService {
	private Collection<Identity> verifiedIdentities = new ConcurrentLinkedQueue<>();
	
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
