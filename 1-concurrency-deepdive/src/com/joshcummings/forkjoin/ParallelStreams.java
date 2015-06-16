package com.joshcummings.forkjoin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelStreams {
	// Own fork join pool 
	
	public List<String> potentiallyInefficientSort() {
		// The implementation of parallel uses the common ForkJoinPool.
		// This means if there are several streams going at once through
		// the application, you could see performance degradation as they
		// all fight for the same pooled threads.
		return 
			Stream.of("xander", "alice", "dave", "eagan", "bobby", "meryl")
				.parallel().sorted().collect(Collectors.toList());
	}
	
	public List<String> betterSort() {
		// a better way is to wrap the parallel execution in its own ForkJoinPool
		// Use the resource at https://www.tobyhobson.co.uk/java-8-parallel-streams-fork-join-pool/
		// to see the syntax for doing this, then add it here.
		return null;
	}
}
