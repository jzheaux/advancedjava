package com.joshcummings.codeplay.concurrency;

import java.security.MessageDigest;

public class HashTimer {
	public static void main(String[] args) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashed = md.digest("many hands make light work".getBytes());
		System.out.println(Hexify.encode(hashed));
		/*URLConnection huc = new URL("http://www.sterlingcrispin.com/100x1000/1000.txt").openConnection();
		Scanner scanner = new Scanner(huc.getInputStream());
		List<String> words = new ArrayList<String>();
		int count = 0;
		int[] lengths = new int[15];
		while ( scanner.hasNextLine() ) {
			String word = scanner.nextLine();
			words.add(word);
			count++;
			lengths[word.length()] = lengths[word.length()] + 1;
		}
		System.out.println(Arrays.toString(lengths));
		*/
		/*
		ForkJoinPool fjp = new ForkJoinPool(8);
		long time = System.nanoTime();
		List<List<String>> sets = new ArrayList<>();
		for ( int i = 0; i < 8; i++ ) {
			sets.add(words.subList(i*words.size()/8, (i+1)*words.size()/8));
		}
		ForkJoinTask<?> fjt = fjp.submit(() ->
				sets.stream().parallel().forEach(set -> {
					set.stream().forEach(word -> {
						words.stream().forEach(word2 -> {
							String withWord2 = word + word2;
							words.stream().forEach(word3 -> {
									try {
										MessageDigest md = MessageDigest.getInstance("SHA-256");
										String attempt = withWord2 + " " + word3 + " " + word + " " + word;
										md.digest(attempt.getBytes());
									} catch ( Exception e ) {
										e.printStackTrace();
									}
							});
						});
					});
				})
		);
		fjt.get();
		//}
		System.out.println((System.nanoTime() - time));*/
		
	}
}
