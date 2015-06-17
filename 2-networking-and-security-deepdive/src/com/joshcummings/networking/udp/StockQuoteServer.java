package com.joshcummings.networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class StockQuoteServer {
	public static void main(String[] args) throws IOException {
		try ( DatagramSocket socket = new DatagramSocket(4445) ) {
			Random r = new Random();
			Map<String, Iterator<Double>> quotes = new HashMap<>();
			
			quotes.put("GOOG",
					Stream.iterate(new Double(234.00), (previous) -> new Double(previous + r.nextDouble() - 0.5))
						.iterator());
			quotes.put("MSFT",
					Stream.iterate(new Double(133.00), (previous) -> new Double(previous + r.nextDouble() - 0.5))
						.iterator());
			quotes.put("YHOO",
					Stream.iterate(new Double(34.00), (previous) -> new Double(previous + r.nextDouble() - 0.5))
						.iterator());
			
			while ( true ) {
				byte[] buf = new byte[4];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String s = new String(packet.getData());
				Iterator<Double> d = quotes.get(s);
				
				if ( d == null ) {
					buf = new byte[0];
				} else {
					buf = d.next().toString().getBytes();
				}
				
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);
				socket.send(packet);
			}		
		}
	}
}
