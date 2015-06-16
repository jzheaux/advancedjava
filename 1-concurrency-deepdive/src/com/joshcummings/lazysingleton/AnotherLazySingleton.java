package com.joshcummings.lazysingleton;

public class AnotherLazySingleton {
	/**
	 * This is a bit of a trick that relies on the class loader.
	 * 
	 * According to the Java spec, AnotherLazySingletonHolder will *NOT*
	 * be initialized until referenced, which means that the instantiation
	 * of the ls variable won't be invoked until getInstance is invoked.
	 * 
	 * @author jzheaux
	 *
	 */
	private static class AnotherLazySingletonHolder {
		private static LazySingleton ls = new LazySingleton();
	}
	
	public LazySingleton getInstance() {
		return AnotherLazySingletonHolder.ls;
	}
}
