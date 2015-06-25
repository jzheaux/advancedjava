package com.joshcummings.async.images;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ImageDownloader {
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private static class ImageInfo {
		String title;
	}
	private static class ImageData {
		byte[] bytes;
	}
	
	public void renderImage(ImageData data) {}
	public void renderText(CharSequence s) {}
	public ImageData downloadImage(ImageInfo info) { return null; }
	public List<ImageInfo> scanForImageInfo(CharSequence source) {
		return new ArrayList<ImageInfo>();
	}
	
	public void renderPage(CharSequence source) {
		List<ImageInfo> info = scanForImageInfo(source);
		// create Callable representing download of all images
		final Callable<List<ImageData>> task = () ->
		  info.stream()
		    	.map(this::downloadImage)
		    	.collect(Collectors.toList());
			// submit download task to the executor
			Future<List<ImageData>> images = executor.submit(task);
			renderText(source);
			try {
			   // get all downloaded images (blocking until all are available)
			   final List<ImageData> imageDatas = images.get();
			   // render images
			   imageDatas.forEach(this::renderImage);
			} catch (InterruptedException e) {
			   // Re-assert the thread’s interrupted status
			   Thread.currentThread().interrupt();
			   // We don’t need the result, so cancel the task too
			   images.cancel(true);
			} catch (ExecutionException e) {
			  throw new RuntimeException(e.getCause());
			}
	}
	
	public void renderPage2(CharSequence source) throws InterruptedException, ExecutionException { 
		   List<ImageInfo> info = scanForImageInfo(source); 
		   CompletionService<ImageData> completionService = 
		     new ExecutorCompletionService<>(executor); 

		   // submit each download task to the completion service 
		   info.forEach(imageInfo -> 
		     completionService.submit(() -> downloadImage(imageInfo))); 
		   
		   renderText(source); 

		   // retrieve each RunnableFuture as it becomes 
		   // available (and when we are ready to process it). 
		   for (int t = 0; t < info.size(); t++) { 
		     Future<ImageData> imageFuture = completionService.take(); 
		     renderImage(imageFuture.get()); 
		   } 
	 }
	
	public void renderPage3(CharSequence source) { 
        List<ImageInfo> info = scanForImageInfo(source); 
        info.forEach(imageInfo -> 
               CompletableFuture 
       		.supplyAsync(() -> downloadImage(imageInfo)) 
       		.thenAccept(this::renderImage)); 
        renderText(source); 
	}
}
