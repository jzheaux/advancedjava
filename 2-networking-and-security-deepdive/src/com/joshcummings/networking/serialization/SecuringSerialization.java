package com.joshcummings.networking.serialization;

import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignedObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;

public class SecuringSerialization {
	/** IMPORTANT NOTE:
	 *  This method is here for convenience. It is NOT intended for copy-paste into a project!
	 * @param s
	 * @return
	 */
	public static SignedObject sign(Serializable s) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		kpg.initialize(1024, random);
		KeyPair pair = kpg.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		Signature signature = Signature.getInstance(priv.getAlgorithm());
		return new SignedObject(s, priv, signature);
	}
	
	/** IMPORTANT NOTE:
	 *  This method is here for convenience. It is NOT intended for copy-paste into a project!
	 * @param s
	 * @return
	 */
	public static SealedObject seal(Serializable s) throws Exception {
		String secret = "secret string";
		String algorithm="AES";
		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		Key key = kg.generateKey();
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return new SealedObject(s, cipher);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		/** Way number one (the hard, but conceptually helpful way):  
		 * 		Wrap the FileOutputStream and InputStreams their respective Encrypting and Decrypting streams
		 */
		Person p = new Person("Phil");
		
		
		/** Way number two (the mostly hard, but super-secure way):
		 * 		Use the SealedObject class and serialize it instead
		 */
	}
}
