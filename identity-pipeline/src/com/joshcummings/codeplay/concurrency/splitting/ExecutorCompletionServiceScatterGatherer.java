package com.joshcummings.codeplay.concurrency.splitting;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.Identity;

public class ExecutorCompletionServiceScatterGatherer implements ScatterGatherer {
	private final ExecutorService pool = Executors.newCachedThreadPool();

	@Override
	public Identity go(Scatterer s, Gatherer g) {
		ExecutorCompletionService<Identity> ecs = new ExecutorCompletionService<>(pool);
		//Queue<Future<Identity>> futures = new LinkedList<>();
		
		int numberOfTasks = 0;
		while ( s.hasNext() ) {
			ecs.submit(s.next());
			numberOfTasks++;
			//Future<Identity> future = pool.submit(s.next());
			//futures.offer(future);
		}
		
		while ( numberOfTasks > 0 && g.needsMore() ) {
			try {
				Identity i = ecs.take().get();//futures.poll().get();
				g.gatherResult(i);
				numberOfTasks--;
			} catch ( InterruptedException | ExecutionException e ) {
				e.printStackTrace();
			}
		}
		
		return g.getFinalResult();
	}
}
