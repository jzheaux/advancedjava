package com.joshcummings.codeplay.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class QueueStreams {
	public static void main(String[] args) {
		Queue<String> q = new LinkedList<>();
		for ( int i = 0; i < 100; i++ ) {
			q.add("hi" + i);
		}
		List<String> first10 = q.stream().limit(10).collect(Collectors.toList());
		System.out.println(q.size());
	}
}
