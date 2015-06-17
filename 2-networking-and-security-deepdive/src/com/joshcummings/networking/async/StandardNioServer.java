package com.joshcummings.networking.async;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandardNioServer {
	public static void main(String[] args) throws Exception {
		// These can be injected and thus configured from without the application
	    ExecutorService connectPool = Executors.newFixedThreadPool(10);
	    Executor readPool = Executors.newCachedThreadPool();
	    Executor writePool = Executors.newCachedThreadPool();

	    AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(connectPool);

    	AsynchronousServerSocketChannel listener = 
             AsynchronousServerSocketChannel.open(group);
        listener.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        listener.bind(new InetSocketAddress(8080));

        listener.accept(null,
        	new CompletionHandler<AsynchronousSocketChannel, Object>() {
				@Override
				public void completed(AsynchronousSocketChannel connection,
						Object attachment) {
					try {
						Thread.sleep(100);
					} catch ( InterruptedException e ) {
						
					}
					listener.accept(null, this);
					CompletableFuture.supplyAsync(() -> { 
                        try {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                            connection.read(buffer).get();
                            return (ByteBuffer) buffer.flip();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new CompletionException(ex);
                        }}, readPool)
                    .thenAcceptAsync((buffer) -> {
                        try {
                            if (buffer == null) return;
                            connection.write(buffer).get();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new CompletionException(ex);
                        }
                    }, writePool)
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
				}

				@Override
				public void failed(Throwable ex, Object attachment) {
					ex.printStackTrace();
				}
        	});
	}
}
