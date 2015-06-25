package com.joshcummings.jms.primefactorization;


public class Factorization {
	private Factorization left;
	private Factorization right;
	private final Long number;
	
	public Factorization(Long number) {
		this.number = number;
	}
	
	 public void setFactors(Factorization left, Factorization right) {
		 this.left = left;
		 this.right = right;
	 }
	 
	 public boolean isComplete() {
		 return isCompleteHelper(this, 1L).equals(number);
	 }
	 
	 public Long isCompleteHelper(Factorization root, Long total) {
		 if ( root == null ) {
			 return total;
		 }
		 if ( root.left == null && root.right == null ) {
			 return total * root.number;
		 }
		 return total * isCompleteHelper(root.left, 1L) * isCompleteHelper(root.right, 1L);
	 }
}
