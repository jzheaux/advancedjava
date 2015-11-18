package com.joshcummings.threading.phaser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MultiPlayerGame {
	private final Queue<Player> playersWaiting = new LinkedList<>();
	private final List<Game> activeGames = new ArrayList<>();
	
	private final CyclicBarrier gameLoader = new CyclicBarrier(2);
	
	private static class Player {
		private final String name;

		public Player(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	private static class Game {
		private final Player one;
		private final Player two;
		private final String guid;
		
		public Game(Player one, Player two) {
			this.one = one;
			this.two = two;
			this.guid = UUID.randomUUID().toString();
			System.out.println("Starting new game.");
		}
		
		 public String getGuid() {
			return guid;
		}
	}
	
	private void addGame() {
		// without synchronizing on read here, we will sometimes poll before the player was offered in registerPlayer
		synchronized ( playersWaiting ) {
			Player one = playersWaiting.poll();
			Player two = playersWaiting.poll();
			Game g = new Game(one, two);
			activeGames.add(g);
			System.out.printf("%s, you are in game #%s%n", one.getName(), g.getGuid());
			System.out.printf("%s, you are in game #%s%n", two.getName(), g.getGuid());
			verify.countDown();
		}
	}
	
	public void registerPlayer(Player p) {
		synchronized ( playersWaiting ) {
			playersWaiting.offer(p);
		}
		
		new Thread(() -> {
			try {
				if ( gameLoader.await() == 0 ) {
					addGame();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private static final CountDownLatch verify = new CountDownLatch(50);
	
	public static void main(String[] args) {
		MultiPlayerGame mpg = new MultiPlayerGame();
		
		ExecutorService es = Executors.newCachedThreadPool();
		for ( int i = 0; i < 101; i++ ) {
			final String name = "player" + i;
			es.submit(() -> {
				Player p = new Player(name);
				mpg.registerPlayer(p);
			});
		}
		
		try {
			verify.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Games started: " + mpg.activeGames.size());
			System.out.println("Players waiting: " + mpg.playersWaiting.size());
		}
		
	}
}
