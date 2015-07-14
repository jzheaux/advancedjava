package com.joshcummings.networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StockQuoteClient {
	public static void main(String[] args) throws IOException {
		try ( DatagramSocket socket = new DatagramSocket() ) {
			byte[] buf = "GOOG".getBytes();
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			socket.send(packet);
			
			buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			
			String received = new String(packet.getData());
			
			System.out.println("Quote received: " + received);
		}
	}
}
