package com.joshcummings.networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class WeatherClient {
	public static void main(String[] args) throws IOException {
		try ( MulticastSocket socket = new MulticastSocket(4446) ) {
			InetAddress group = InetAddress.getByName("230.0.0.1");
			socket.joinGroup(group);
			
			DatagramPacket packet;
			for ( int i = 0; i < 100; i++ ) {
				byte[] buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				System.out.println("Current Temperature: " + new String(buf));
			}
					
		}
	}
}
