package com.joshcummings.codeplay.concurrency.throttle;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.joshcummings.codeplay.concurrency.Address;
import com.joshcummings.codeplay.concurrency.AddressVerifier;
import com.joshcummings.codeplay.concurrency.NoValidAddressesException;

public class BackpressureAddressVerifier implements AddressVerifier {

	private ThreadPoolExecutor pool = new ThreadPoolExecutor(
			20, 20, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),
			new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					throw new NoValidAddressesException();
				}
				
			});

	private AddressVerifier delegate;
	
	@Override
	public void verify(List<Address> address) {
		pool.submit(() -> {
			delegate.verify(address);
		});
	}
}
