package com.joshcummings.networking.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializationIsInsecure {
	public static void main(String[] args) throws Exception {
		Person obj = new Person("Dave");
		File file = new File("object.out");
		try ( ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)); ) {
			oos.writeObject(obj);
		}
		
		/** Now go into a Hex editor and edit the person data, and then run SerializationIsInsecurePartTwo **/
	}
}
