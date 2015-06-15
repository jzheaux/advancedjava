package org.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.joshcummings.networking.nonblocking.PlacesToGo;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Timeout(time=10, timeUnit=TimeUnit.SECONDS)
public class MyBenchmark {

	@Param({ "1000" })
	public int size;

	private PlacesToGo ptg = new PlacesToGo();
	
	@Setup
	public void setup() throws IOException {
	}

	@Benchmark
	public String syncEchoServer() throws IOException {       
		
		try ( Socket s = new Socket("127.0.0.1", 8080) ) {
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.println("Hi!");
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			return br.readLine();
		}
	}

	@Benchmark
	public String asyncNioEchoServer() throws IOException {
		
		try ( Socket s = new Socket("127.0.0.1", 8081) ) {
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.println("Hi!");
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			return br.readLine();
		}
	}

	@Benchmark
	public String asyncEchoServer() throws IOException {
		
		try ( Socket s = new Socket("127.0.0.1", 8082) ) {
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.println("Hi!");
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			return br.readLine();
		}
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(
				MyBenchmark.class.getSimpleName()).build();

		new Runner(opt).run();
	}

}