package com.joshcummings.networking.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CanWeDoBetterWithCompletableFuture {
	public static void asyncServerLoop(int port) throws IOException {
		ServerSocket ss = new ServerSocket(port);
	    Executor readPool = Executors.newCachedThreadPool();
	    Executor writePool = Executors.newCachedThreadPool();
		processConnection(ss, readPool, writePool);
	}
	
	private static void processConnection(ServerSocket ss, Executor readPool, Executor writePool) throws IOException {
		CompletableFuture.supplyAsync(() -> {
			try{
				return ss.accept();
			} catch ( IOException e ) {
				throw new CompletionException(e);
			}
		}).thenAcceptAsync((s) -> {
			CompletableFuture.supplyAsync(() -> { 
                try {
    				processConnection(ss, readPool, writePool);
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    return br.readLine();
                } catch (IOException ex) {
                    throw new CompletionException(ex);
                }
            }, readPool)
            .thenAcceptAsync((input) -> {
                try {
                    PrintWriter pw  = new PrintWriter(s.getOutputStream(), true);
                    pw.println(input);
                } catch (IOException ex) {
                    throw new CompletionException(ex);
                }
            }, writePool)
            .exceptionally(ex -> {
                ex.printStackTrace(); 
                return null;
            });
		});
	}

	public static void main(String[] args) throws Exception {
		asyncServerLoop(8080);
	}
}
