package com.joshcummings.networking.serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class ItsImportantToUseSerialVersionUID {
	private static class UserAccount implements Serializable {
		private final String username;
		private final char[] password;
		
		public UserAccount(String username, char[] password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public String toString() {
			return "UserAccount [username=" + username + ", password="
					+ Arrays.toString(password) + "]";
		}
	}
	
	public void writeUserAccountToFile(UserAccount ua) throws IOException {
		try ( ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("file.out"))) {
			oos.writeObject(ua);
		}
	}
	
	public UserAccount readUserAccountFromFile() throws IOException, ClassNotFoundException {
		try ( ObjectInputStream ois = new ObjectInputStream(new FileInputStream("file.out"))) {
			return (UserAccount)ois.readObject();
		}
	}
	
	/**
	 * This setup works for now because no changes have been made to the structure of UserAccount object
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ItsImportantToUseSerialVersionUID i = new ItsImportantToUseSerialVersionUID();
		
		try {
			UserAccount b = i.readUserAccountFromFile();
			System.out.println(b);
		} catch ( FileNotFoundException e ) {
			System.out.println("First time run, no file");
		}
		
		UserAccount a = new UserAccount("bobs", "yeruncle".toCharArray());
		i.writeUserAccountToFile(a);
		System.out.println(a);
		
		
	}
}
