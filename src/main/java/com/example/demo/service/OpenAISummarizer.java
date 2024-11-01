package com.example.demo.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.SummarizerMessages;
import com.example.demo.store.OpenAIStore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.sashirestela.openai.common.ResponseFormat;
import io.github.sashirestela.openai.common.ResponseFormat.JsonSchema;
import io.github.sashirestela.openai.domain.chat.ChatRequest;



@Service
public class OpenAISummarizer {
	
	
	@Autowired 
	private OpenAIStore openAiStore;
	
	@Autowired
	private SummarizerMessages summarizerMessages;
	
	public OpenAISummarizer(OpenAIStore openAiStore) {
		this.openAiStore = openAiStore;
    }
	
	public String getResponse(String userQuestion, String sqlTable) {
	    	
    	var userMsg = """
    			userQuestion:
    			 
    			%s
    			\n
    			SqlTable:
    			
    			%s
    			\n
    			Add html tags which can be rendered directly in my html code so that it can look arranged.
    			""".formatted(userQuestion, sqlTable);
    	
    	
    	summarizerMessages.setMessages(userMsg, true);
    	
    	var chatResponse = this.openAiStore.getOpenAI().chatCompletions()
                .create(ChatRequest.builder()
                        .model("gpt-4o-2024-08-06")
                        .messages( summarizerMessages.getMessages())
                        .temperature(0.0)
                        .stream(false)
                        .responseFormat(ResponseFormat.jsonSchema(JsonSchema.builder()
                                .name("Summarizer")
                                .schemaClass(Summarizer.class)
                                .build()))
                        .build())
                .join();
    	
  
 
    	
    	JSONObject jsonResponse = new JSONObject(chatResponse.getChoices().get(0).getMessage().getContent().toString());
    	String summary = jsonResponse.get("summary").toString();
	
    	this.summarizerMessages.setMessages(chatResponse.getChoices().getFirst().getMessage().getContent(), false);
  
		return summary;
  
    }

	public static class Summarizer {
		
		 @JsonPropertyDescription("Summary of table data as per user requirement")
	     @JsonProperty(required = true)
	     public String summary;
		 
		 public Summarizer(String summary) {
			 this.summary = summary;
			 
		 
		}
	}


}
