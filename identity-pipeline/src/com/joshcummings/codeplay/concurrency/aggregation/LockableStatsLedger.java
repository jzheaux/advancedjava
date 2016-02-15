package com.joshcummings.codeplay.concurrency.aggregation;

import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.joshcummings.codeplay.concurrency.StatsLedger;

public class LockableStatsLedger implements StatsLedger {
	private StatsLedger delegate;
	private ReentrantLock lock = new ReentrantLock();

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public LockableStatsLedger(StatsLedger delegate) {
		this.delegate = delegate;
		scheduler.scheduleAtFixedRate(this::publish, 1000, 5000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void recordEntry(StatsEntry entry) {
		lock.lock();
		try {
			delegate.recordEntry(entry);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Integer getRecordCount() {
		return delegate.getRecordCount();
	}
	
	public void publish() {
		System.out.println("Number of Records: " + getRecordCount());
	}

}
