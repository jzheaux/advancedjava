package com.joshcummings.codeplay.concurrency.splitting;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.Identity;

public class ExecutorCompletionServiceScatterGatherer implements ScatterGatherer {
	private ExecutorService pool = Executors.newCachedThreadPool();

	public Identity go(Scatterer scatterer, Gatherer gatherer) {
		ExecutorCompletionService<Identity> ecs = new ExecutorCompletionService<>(pool);
		fireTasks(scatterer, ecs);
		return gatherResults(gatherer, ecs);
	}
	
	private <PART> void fireTasks(Scatterer scatterer, ExecutorCompletionService<Identity> ecs) {
		while ( scatterer.hasNext() ) {
			ecs.submit(scatterer.next());
		}
	}
	
	private Identity gatherResults(Gatherer gatherer, ExecutorCompletionService<Identity> ecs) {
		while ( gatherer.needsMore() ) {
			try {
				Identity result = ecs.take().get();
				gatherer.gatherResult(result);
			} catch ( InterruptedException e ) {
				Thread.currentThread().interrupt();
			} catch ( ExecutionException e ) {
				// log and move on to the next task
			}
		}
		return gatherer.getFinalResult();
	}

}
