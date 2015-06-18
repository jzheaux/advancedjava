package com.joshcummings.di.circulardependency;

public class BeanA {
	private final BeanB beanB;
	
	public BeanA(BeanB beanB) {
		this.beanB = beanB;
	}
}
