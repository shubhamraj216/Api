package com.hitit.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HitController extends Thread {
	Logger logger = LoggerFactory.getLogger(HitController.class);
	
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello";
	}
	
	@PostMapping("/apis")
	public long hitApi(@RequestBody Hit[] hits) {
		long initial = System.currentTimeMillis();
//		long responseTimeTheoretical = 0;
		
		// 1. HttpURLConnection
		// 2. HttpClient
		
		HttpClient client = HttpClient.newHttpClient();
		for(Hit hit: hits) {
			// Sequential Requests
			if(!hit.isParallel()) {
				for(int i = 0; i < hit.getCount(); i++) {
					try {
						HttpRequest request = HttpRequest.newBuilder(hit.getUrl()).build();		
						CompletableFuture<HttpResponse<String>> res = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
						
						// do some other tasks instead of waiting for res to complete
						logger.info(hit.getUrl() + " hitted");
						
						res.get();
						logger.info(hit.getUrl() + " got response " + res.isDone());
						
//						responseTimeTheoretical += Long.parseLong(res.get().body());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
						throw new RuntimeException();
					}
				}
			} 
			// Parallel Requests
			else {
				List<CompletableFuture<HttpResponse<String>>> res = new ArrayList<CompletableFuture<HttpResponse<String>>>();
				for(int i = 0; i < hit.getCount(); i++) {
					HttpRequest request = HttpRequest.newBuilder(hit.getUrl()).build();		
					res.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
					
					logger.info(hit.getUrl() + " hitted");
				}
				
				// do some other tasks instead of waiting for res to complete
				
				try {
					for(int i = 0; i < hit.getCount(); i++) {
						res.get(i).get();
						logger.info(hit.getUrl() + " got response " + res.get(i).isDone());
					}
					
//					responseTimeTheoretical += Long.parseLong(res.get(0).get().body());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		}
		
		
		long timeTakenToCompleteFlow = System.currentTimeMillis() - initial;
//		 return responseTimeTheoretical;
		return timeTakenToCompleteFlow;
	}
	
}
