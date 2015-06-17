package com.joshcummings.networking.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class SerializationIsInsecurePartTwo {
	public static void main(String[] args) throws Exception {
		File file = new File("object.out");
		try ( ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)); ) {
			System.out.println(ois.readObject());
		}
	}
}
