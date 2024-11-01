package com.example.demo.store;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.sashirestela.openai.SimpleOpenAI;

@Component
public class OpenAIStore {
	
	private final String apiKey;
	
	private final SimpleOpenAI openAI;
	
	public OpenAIStore() {
		
		final Dotenv dotenv = Dotenv.load();
    	this.apiKey = dotenv.get("OPENAI_API_KEY");
	
		this.openAI = SimpleOpenAI.builder()
	    	    .apiKey(this.apiKey)
	    	    .build();
	}
	
	public SimpleOpenAI getOpenAI() {
		
		return this.openAI;
	}
}
