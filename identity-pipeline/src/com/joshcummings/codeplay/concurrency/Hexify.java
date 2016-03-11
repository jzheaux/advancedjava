package com.joshcummings.codeplay.concurrency;

/*
 * Copyright (C) 2002 Nick Galbreath. All Rights Reserved.
 * 
 * Orginally published in or based on works from the book Cryptography for
 * Internet and Database Applications by Nick Galbreath, Wiley Publishing, 2002
 * ISBN 0-471-21029-3 See http://www.modp.com or
 * http://www.wiley.com/compbooks/galbreath for details.
 * 
 * This software is provided as-is, without express or implied warranty.
 * Permission to use, copy, modify, distribute or sell this software, without
 * fee, for any purpose and by any individual or organization, is hereby
 * granted, provided that the above copyright notice, the original publication
 * information and this paragraph (i.e. this entire Java comment) appear in all
 * copies.
 *  
 */

/**
 * A fast converter of binary array into the standard hexadecimal digits.
 * 
 * For example 0xf32a --> "f32a".
 * 
 * <p>
 * For more information see Chapter 1, pages 26-27 in <i>Cryptography for
 * Internet and Database Applications
 * 
 * @author Nick Galbreath, http://www.modp.com/
 * @version 1.0.1
 *  
 */
public class Hexify {
        protected static char[] hexDigits =
                {
                        '0',
                        '1',
                        '2',
                        '3',
                        '4',
                        '5',
                        '6',
                        '7',
                        '8',
                        '9',
                        'A',
                        'B',
                        'C',
                        'D',
                        'E',
                        'F' };

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
         *             if the String contains non-hexadecimal digital (i.e. not
         *             0-9, A-F or a-f)
         */
        public static byte[] decode(String s) throws Exception {
                char[] input = s.toCharArray();
                int max = input.length;
                int odd = max & 0x01;
        //      byte b;
                byte[] buf = new byte[max / 2 + odd];
                int i = 0, j = 0;
                if (odd == 1) {
                        buf[j++] = (byte) getHexDecode(input[i++]);
                }
                while (i < max) {
                        buf[j++] =
                                (byte) ((getHexDecode(input[i++]) << 4)
                                        | getHexDecode(input[i++]));
                }
                return buf;
        }
}
