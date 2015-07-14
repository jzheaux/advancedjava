package com.joshcummings.security.crypto;

import java.security.MessageDigest;
import java.util.IllegalFormatException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class Hashing {
	protected static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	protected static int[] hexDecode = new int[256];

	static {
		for (int i = 0; i < 256; ++i)
			hexDecode[i] = -1;
		for (int i = '0'; i <= '9'; ++i)
			hexDecode[i] = i - '0';
		for (int i = 'A'; i <= 'F'; ++i)
			hexDecode[i] = i - 'A' + 10;
		for (int i = 'a'; i <= 'f'; ++i)
			hexDecode[i] = i - 'a' + 10;
	}

	/**
	 * a table lookup function
	 */
	protected static int getHexDecode(char c) throws Exception {
		int x = hexDecode[c];
		if (x < 0)
			throw new Exception("Bad hex digit " + c);
		return x;
	}

	/**
	 * Encodes a binary array into a hexadecimal string
	 * 
	 * @param b
	 *            The input byte array
	 * @return a string containing hexadecimal digits (0-9, A-F)
	 */
	public static String encode(byte[] b) {
		char[] buf = new char[b.length * 2];
		int max = b.length;
		int j = 0;
		for (int i = 0; i < max; ++i) {
			buf[j++] = hexDigits[(b[i] & 0xf0) >> 4];
			buf[j++] = hexDigits[b[i] & 0x0f];
		}
		return new String(buf);
	}

	/**
	 * Decodes a hexadecimal digit string back into the original bytes
	 * 
	 * @param s
	 *            The hexadecimal digit string to decode.
	 * @return a byte array containing the orignal binary data
	 * @throws IllegalFormatException
	 *             if the String contains non-hexadecimal digital (i.e. not 0-9,
	 *             A-F or a-f)
	 */
	public static byte[] decode(String s) throws Exception {
		char[] input = s.toCharArray();
		int max = input.length;
		int odd = max & 0x01;
		// byte b;
		byte[] buf = new byte[max / 2 + odd];
		int i = 0, j = 0;
		if (odd == 1) {
			buf[j++] = (byte) getHexDecode(input[i++]);
		}
		while (i < max) {
			buf[j++] = (byte) ((getHexDecode(input[i++]) << 4) | getHexDecode(input[i++]));
		}
		return buf;
	}

	private static final Pattern pattern = Pattern.compile(" was succesfully reversed into the string « (.*) ».");
	
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter password: ");
		String password = input.nextLine();
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digested = md.digest(password.getBytes());
		String md5 = encode(digested);
		System.out.println("Hashed password: " + md5 + ", irreversible?");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet("http://md5.gromweb.com/?md5=" + md5);
		String body = IOUtils.toString(client.execute(get).getEntity().getContent());
		
		Matcher m = pattern.matcher(body);
		if ( m.find() ) {
			System.out.println("Password: " + m.group(1));
		}
	}
}
