package com.joshcummings.codeplay.concurrency.splitting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.joshcummings.codeplay.concurrency.Identity;

public class ExecutorServiceScatterGatherer implements ScatterGatherer {	
	private ExecutorService pool = Executors.newCachedThreadPool();

	@Override
	public Identity go(Scatterer scatterer, Gatherer gatherer) {
		BlockingQueue<Future<Identity>> futures = new LinkedBlockingQueue<>();
		fireTasks(scatterer, futures);
		return gatherResults(gatherer, futures);
	}
	
	private void fireTasks(Scatterer scatterer, BlockingQueue<Future<Identity>> futures) {
		while ( scatterer.hasNext() ) {
			futures.add(pool.submit(scatterer.next()));
		}
	}
	
	private Identity gatherResults(Gatherer gatherer, BlockingQueue<Future<Identity>> futures) {
		while ( gatherer.needsMore() ) {
			try {
				gatherer.gatherResult(futures.take().get());
			} catch ( InterruptedException e ) {
				Thread.currentThread().interrupt();
			} catch ( ExecutionException e ) {
				// log and move on to the next task
			}
		}
		return gatherer.getFinalResult();
	}
}
