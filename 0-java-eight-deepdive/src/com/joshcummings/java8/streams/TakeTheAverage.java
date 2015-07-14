package com.joshcummings.java8.streams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TakeTheAverage {
	private List<Integer> values = new ArrayList<>();
	
	public TakeTheAverage(Integer... values) {
		this.values = Arrays.asList(values);
	}
	
	public double java7Average() {
		double sum = 0d;
		for ( Integer v : values ) {
			sum += v;
		}
		return values.isEmpty() ? 0 : sum / values.size();
	}
	
	public double java8Average() {
		DoubleHolder sum = values.stream().collect(DoubleHolder::new, (left, right) -> left.add(right), (left, right) -> left.add(right));
		
		return values.isEmpty() ? 0 : sum.getHeld() / values.size();
		/*
		double sum = values.stream().reduce(0, (left, right) -> left + right);
		
		return values.isEmpty() ? 0 : sum / values.size();*/
	}
	
	private static class DoubleHolder {
		double held = 0;
		
		public void add(double to) {
			held += to;
		}
		
		public void add(DoubleHolder to) {
			held += to.held;
		}
		
		public double getHeld() {
			return held;
		}
	}
	
	public double java8ThreadedAverage() {
		DoubleHolder sum = values.parallelStream().collect(DoubleHolder::new, (left, right) -> left.add(right), (left, right) -> left.add(right));
		
		return values.isEmpty() ? 0 : sum.getHeld() / values.size();
	}
}
