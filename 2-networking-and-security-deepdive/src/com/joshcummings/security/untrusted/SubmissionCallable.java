package com.joshcummings.security.untrusted;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SubmissionCallable implements Callable<Submission> {
	private static final Logger logger = LoggerFactory.getLogger(SubmissionCallable.class);
	
	private Submission submission;
	private Set<Test> tests;
	private File baseDir;

	public SubmissionCallable(Submission submission, Set<Test> tests) {
		this.submission = submission;
		this.tests = tests;
		this.baseDir = new File(".");
	}
	
	public SubmissionCallable(Submission submission, Set<Test> tests, File baseDir) {
		this.submission = submission;
		this.tests = tests;
		this.baseDir = baseDir;
	}
	
	private String getCompilationCommand(Language language) {
		if ( language == Language.JAVA ) {
			String javaHome = System.getProperty("java.home");
			File file = new File(javaHome);
			File javac = new File(new File(file.getParent(), "bin"), "javac");
			
			return javac.getAbsolutePath();
		} else if ( language == Language.CSHARP ) {
			return "mcs";
		} else {
			return null;
		}
	}
	
	private String[] getExecutionCommand(Language language) {
		if ( language == Language.JAVA ) {
			String javaHome = System.getProperty("java.home");
			File file = new File(javaHome);
			File java = new File(new File(file.getParent(), "bin"), "java");
			
			return new String[] { java.getAbsolutePath(), "Solution" };
		} else if ( language == Language.CSHARP ) {
			return new String[] { "mono", "Solution.exe" };
		} else {
			return null;
		}
	}
	
	@Override
	public Submission call() {
		File solution = new File(submission.getLocation());
		File parent = solution.getParentFile();
		File errors = new File(parent, "errors");
				
		try {
			errors.createNewFile();
			
			String compilationCommand = getCompilationCommand(submission.getLanguage());
			
			String[] compilation = { compilationCommand, solution.getName() };
			ProcessBuilder pb = new ProcessBuilder().command(compilation).directory(parent.getAbsoluteFile()).redirectError(errors);

			Process p = pb.start();
			int code = p.waitFor();
			
			List<String> errorMessages = Files.readAllLines(Paths.get(errors.getAbsolutePath()));
		
			if ( errorMessages.isEmpty() ) {
				ExecutorService executors = Executors.newFixedThreadPool(2);
				
				String[] execution = getExecutionCommand(submission.getLanguage());
				for ( Test test : tests ) {
					try {
						pb = new ProcessBuilder().command(execution).directory(parent).redirectError(errors);
						p = pb.start();
		
						PrintWriter pw = new PrintWriter(p.getOutputStream(), true);
						BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

						// rewrite this so we can run all futures for
						// a given submission at once
						Long time = System.currentTimeMillis();
						Future<String> output = executors.submit(() -> {
								logger.debug("Beginning test input {0} for submission {1}", test.getInput(), submission.getLocation());
								pw.println(test.getInput());
								logger.debug("Sent input {0} to submission {1}", test.getInput(), submission.getLocation());
								String result = br.readLine();
								logger.debug("Retreived output for input {0} to submission {1}", test.getInput(), submission.getLocation());
								return result;
							});
						
						try {
							String response = output.get(test.getMaxTime(), TimeUnit.MILLISECONDS);
							errorMessages = Files.readAllLines(Paths.get(errors.getAbsolutePath()));
							
							if ( errorMessages.isEmpty() ) {
								submission.addResult(test, response, System.currentTimeMillis() - time);
							} else {
								submission.addResult(test, errorMessages);
							}
						} catch ( TimeoutException | ExecutionException e ) {
							submission.addResult(test, Arrays.asList(e.getMessage()));
						} finally {
							p.destroyForcibly();
						}
						
						
					} catch ( InterruptedException e ) {
						submission.addResult(test, Arrays.asList(e.getMessage()));
					}
				}
				
				executors.shutdown();
			} else {
				submission.addErrors(errorMessages);
			}
		} catch ( Throwable e ) {
			submission.addErrors(Arrays.asList(e.getMessage()));
		}
		
		return submission;
	}

}
