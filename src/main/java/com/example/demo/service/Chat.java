package com.example.demo.service;


import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Messages;
import com.example.demo.model.Schema;
import com.example.demo.repository.SchemaRepository;
import com.example.demo.store.OpenAIStore;
import com.example.demo.store.SchemaIdStore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.sashirestela.openai.common.ResponseFormat;
import io.github.sashirestela.openai.common.ResponseFormat.JsonSchema;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;

@Service
public class Chat {
	
	@Autowired 
	private final OpenAIStore openAiStore;
	
	@Autowired
    private SchemaIdStore schemaIdStore;
	
	@Autowired
	private OpenAISummarizer openAiSummarizer;
	
	@Autowired
	Messages message;
	
	@Autowired
	SchemaRepository schemaRepository;
	
    private SqlQueryExecutor executor;
    
    
    public Chat(OpenAIStore openAiStore) {
    	this.openAiStore = openAiStore;
    	executor = new SqlQueryExecutor();
    }
    
    public String getResponse(String userMessage, String modelName, String id) {
    	
    	Optional<Schema> data = schemaRepository.findById(id);
    	
    	Schema schema = data.get();
    
    	
    	var userMsg = """ 
    			%s
    			\n
    			%s
    			""".formatted(schema.getSchema() , userMessage);
    	

    	
    	message.setMessages(userMsg, true);
    	
    	var chatResponse = this.openAiStore.getOpenAI().chatCompletions()
                .create(ChatRequest.builder()
                        .model(modelName)
                        .messages( message.getMessages())
                        .temperature(0.0)
                        .stream(false)
                        .responseFormat(ResponseFormat.jsonSchema(JsonSchema.builder()
                                .name("SqlQuery")
                                .schemaClass(SqlQuery.class)
                                .build()))
                        .build())
                .join();
  
    
    	  
//        System.out.println(chatResponse.getChoices().get(0).getMessage().getContent().toString());
//    	System.out.println(chatResponse.getChoices().get(0).getMessage().getContent().toString());
//    	System.out.println(chatResponse.firstMessage().getContent().toString());
    	
    	JSONObject jsonResponse = new JSONObject(chatResponse.getChoices().get(0).getMessage().getContent().toString());
    	
    	String sqlQuery = jsonResponse.get("query").toString();
	
    	this.message.setMessages(chatResponse.getChoices().getFirst().getMessage().getContent(), false);
    	
    	String result = executor.executeSqlQuery(sqlQuery);
    	
    	String summary = openAiSummarizer.getResponse(userMessage, result);
    
        System.out.println(sqlQuery);
        
        
		return summary;
  
    }

	
    
    public static class SqlQuery {
    
    	 @JsonPropertyDescription("Postgres sql query only")
         @JsonProperty(required = true)
         public String query;
    	 
    	 public SqlQuery(String sqlQuery) {
    		 this.query = sqlQuery;
    		 
    	}
    }

}
