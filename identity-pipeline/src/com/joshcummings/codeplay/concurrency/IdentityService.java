package com.joshcummings.codeplay.concurrency;

import java.util.List;
import java.util.function.Predicate;

public interface IdentityService {
	boolean persistOrUpdateBestMatch(Identity identity);

	Identity getOne(Predicate<Identity> pred);
	List<Identity> search(Predicate<Identity> pred);
	
	public class MergeCandidate implements Comparable<MergeCandidate> {
		private final Identity candidate;
		private final Integer score;
		
		public MergeCandidate(Identity candidate, Integer score) {
			this.candidate = candidate;
			this.score = score;
		}
		public Identity getCandidate() {
			return candidate;
		}
		@Override
		public int compareTo(MergeCandidate that) {
			return this.score.compareTo(that.score);
		}
	}
}
