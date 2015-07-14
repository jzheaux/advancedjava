package com.joshcummings.java8.lambda;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joshcummings.java8.streams.TakeTheAverage;
import com.joshcummings.java8.streams.TakeTheAveragePrimitive;


public class Benchmarker {
	public <V> BenchmarkResults benchmark(Integer runs, Runnable r) {
		BenchmarkResults results = new BenchmarkResults(runs);
		for ( int i = 0; i < runs; i++ ) {
			r.run(); // warm up the JVM
		}
		for ( int i = 0; i < runs; i++ ) {
			long time = System.nanoTime();
			r.run();
			results.add(i, System.nanoTime() - time);
		}
		return results;
	}
	
	public static class BenchmarkResults {
		private Long[] results;
		private Long sum = 0L;
		private Long max;
		
		public BenchmarkResults(Integer i) {
			results = new Long[i];
		}
		
		public void add(Integer i, Long time) {
			results[i] = time;
			sum += time;
			if ( max == null || max < time ) {
				max = time;
			}
		}
		
		public BenchmarkResults sample(int size) {
			if ( size > results.length ) return this;
			
			BenchmarkResults sample = new BenchmarkResults(size);
			for ( int i = 0; i < size; i++ ) {
				int index = results.length / size * i;
				sample.add(i, results[index]);
			}
			return sample;
		}
		
		public long max() {
			return max;
		}
		
		public double average() {
			return sum / results.length;
		}
		
		public double stddev() {
			return Math.sqrt(
					(1.0 / results.length) * 
					Arrays.asList(results).stream()
						.reduce(0L, (left, right) -> 
										left + (int)Math.pow( right - average(), 2 )));
		}
		
		public Long[] results() {
			return results;
		}
	}
	
	private static void show(BenchmarkResults br) {
		JFrame frame = new JFrame("Results");
		frame.setLayout(new BorderLayout());
		frame.add(new Chart(br), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static class Chart extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private BenchmarkResults br;
		
		public Chart(BenchmarkResults br) {
			this.br = br;
			super.setPreferredSize(new Dimension(800, 800));
			super.setBackground(Color.WHITE);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			int y1 = 800;
			Long[] results = br.results();
			for ( int i = 0; i < 800; i++ ) {
				int index = (int)(( results.length / 800.0 ) * i);
				int y2 = 800 - (int)(( results[index] * 800.0 ) / br.max());
				g.drawLine(i, y1, i+1, y2);
				y1 = y2;
			}
			g.drawString(String.valueOf(br.max()) + "ns", 0, 12);
			double avg = br.average();
			int y = 800 - (int)(avg * 800 / br.max());
			g.setColor(Color.RED);
			g.setFont(Font.decode("Arial-BOLD"));
			g.drawLine(0, y, 800, y);
			g.drawString(String.valueOf(avg) + "ns", 0, y - 10);
		}
	}
		
	public static void main(String[] args) {
		Benchmarker b = new Benchmarker();
		
		int[] valuesP = new int[100];
		Integer[] values = new Integer[100];
		Random r = new Random();
		for ( int i = 0; i < values.length; i++ ) {
			values[i] = i;
			valuesP[i] = i;
		}
		TakeTheAverage tta = new TakeTheAverage(values);
		TakeTheAveragePrimitive ttap = new TakeTheAveragePrimitive(valuesP);
		
		//BenchmarkResults br = b.benchmark(10000000, () -> ttap.java8Average());
		BenchmarkResults br2 = b.benchmark(10000000, () -> ttap.java7Average());

		//show(br.sample(800));
		show(br2.sample(800));
	}
}
