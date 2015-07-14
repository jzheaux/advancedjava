package com.joshcummings.networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.stream.Stream;

public class WeatherData {
	public static void main(String[] args) throws IOException {
		try ( DatagramSocket socket = new DatagramSocket(4445) ) {
			Random r = new Random();
			Stream.iterate(new Double(83.2421), (previous) -> new Double(previous + r.nextDouble() - 0.5))
				.forEach((temperature) -> {
					byte[] buf = String.valueOf(temperature).getBytes();
					
					try {
						InetAddress group = InetAddress.getByName("230.0.0.1");
						DatagramPacket packet =
							new DatagramPacket(buf, buf.length, group, 4446);
						socket.send(packet);
					} catch ( IOException e ) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
					
					try {
						Thread.sleep(r.nextInt(3000));
					} catch ( InterruptedException e ) {
						Thread.currentThread().interrupt();
					}
				});
					
		}
	}
}
