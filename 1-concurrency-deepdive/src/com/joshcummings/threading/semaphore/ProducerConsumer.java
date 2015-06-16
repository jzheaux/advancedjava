package com.joshcummings.threading.semaphore;

import java.util.concurrent.Semaphore;

public class ProducerConsumer {

	private static int items;
	
	public static void main(String[] args) throws Exception {

		Semaphore semaphoreProducer = new Semaphore(1);
		Semaphore semaphoreConsumer = new Semaphore(0);
		System.out
				.println("semaphoreProducer permit=1 | semaphoreConsumer permit=0");

		Producer producer = new Producer(semaphoreProducer, semaphoreConsumer);
		Consumer consumer = new Consumer(semaphoreConsumer, semaphoreProducer);

		Thread producerThread = new Thread(producer, "ProducerThread");
		Thread consumerThread = new Thread(consumer, "ConsumerThread");

		producerThread.start();
		consumerThread.start();
		
		producerThread.join();
		consumerThread.join();
		
		System.out.println(items);

	}

	/**
	 * Producer Class.
	 */
	private static class Producer implements Runnable {

		Semaphore semaphoreProducer;
		Semaphore semaphoreConsumer;

		public Producer(Semaphore semaphoreProducer, Semaphore semaphoreConsumer) {
			this.semaphoreProducer = semaphoreProducer;
			this.semaphoreConsumer = semaphoreConsumer;
		}

		public void run() {
			for (int i = 1; i <= 100000; i++) {
				try {
					semaphoreProducer.acquire();
					//System.out.println("Produced : " + i);
					items++;
					semaphoreConsumer.release();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Consumer Class.
	 */
	private static class Consumer implements Runnable {

		Semaphore semaphoreConsumer;
		Semaphore semaphoreProducer;

		public Consumer(Semaphore semaphoreConsumer, Semaphore semaphoreProducer) {
			this.semaphoreConsumer = semaphoreConsumer;
			this.semaphoreProducer = semaphoreProducer;
		}

		public void run() {

			for (int i = 1; i <= 100000; i++) {
				try {
					semaphoreConsumer.acquire();
					items--;
					semaphoreProducer.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
