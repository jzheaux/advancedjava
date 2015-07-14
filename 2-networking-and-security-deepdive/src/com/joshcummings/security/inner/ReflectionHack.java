package com.joshcummings.security.inner;

import java.lang.reflect.Method;


public class ReflectionHack {
	public static void main(String[] args) throws Exception {
		OuterClass oc = new OuterClass();
		Class<?> clazz = oc.getClass();

		Method[] methods = clazz.getDeclaredMethods();
		for ( int i = 0; i < methods.length; i++ ) {
			if ( methods[i].isSynthetic() ) {
				System.out.println("Found protected synthetic method:\n " + methods[i]);
				System.out.println("\t--> Successfully accessed private variable! " + methods[i].invoke(oc, oc));
			}
		}

		methods = oc.getA().getClass().getDeclaredMethods();
		for ( int j = 0; j < methods.length; j++ ) {
			if ( methods[j].isSynthetic() ) {
				System.out.println("Found protected synthetic method:\n " + methods[j]);
				System.out.println("\t--> Successfully accessed private variable! " + methods[j].invoke(oc.getA(), oc.getA()));
			}
		}
	}
}
