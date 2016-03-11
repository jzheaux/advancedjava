package com.joshcummings.codeplay.concurrency.throttle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class Batcher<T extends Observable> {
	/* The backlog of jobs to perform */
	private BlockingQueue<T> jobQueue = new LinkedBlockingQueue<>();
	
	/* The cyclic barrier causes {batchSize} threads to wait at a time */
	private final CyclicBarrier batcher;

	private final int batchSize;
	private final int perVerifyWaitTime;
	
	private final ExecutorService es = Executors.newCachedThreadPool();
	
	public Batcher(int batchSize, int perVerifyWaitTime) {
		this.batchSize = batchSize;
		this.perVerifyWaitTime = perVerifyWaitTime;
		batcher = new CyclicBarrier(batchSize);
	}
	
	/**
	 * Pull {batchSize} elements from the queue
	 * 
	 * @param queue
	 * @return
	 */
	private List<T> pollBatch(Queue<T> queue) {
		List<T> batch = new ArrayList<>(batchSize);
		jobQueue.drainTo(batch, batchSize);
		/*int count = 0;
		while ( count < batchSize ) {
			batch.add(queue.poll());
			count++;
		}*/
		return batch;
	}
	
	public void batch(List<T> jobs, Consumer<List<T>> action) {
		Queue<T> overflow = new ConcurrentLinkedQueue<>(jobs);
		CountDownLatch cdl = new CountDownLatch(jobs.size());
		
		jobs.stream()
			.forEach(job -> {
				job.addObserver(
					(observable, arg) -> {
						overflow.remove(job);
						cdl.countDown();
					});
				
				jobQueue.add(job);
				es.submit(() -> {
					try {
						if ( batcher.await(perVerifyWaitTime, TimeUnit.MILLISECONDS) == 0 ) {
							action.accept(pollBatch(jobQueue));
						}
					}
					catch (TimeoutException | BrokenBarrierException | InterruptedException e) {
						cdl.countDown();
					}
				});
			});
		
		try {
			cdl.await(perVerifyWaitTime*overflow.size(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if ( !overflow.isEmpty() ) {
			System.out.println("Processing an overflow of size: " + overflow.size());
			action.accept(new ArrayList<>(overflow));
		}
	}
	
	public void close() {
		es.shutdown();
	}
}
