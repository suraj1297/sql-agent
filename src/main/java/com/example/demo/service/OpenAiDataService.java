package com.example.demo.service;

import com.example.demo.model.Schema;
import com.example.demo.model.TrainData;
import com.example.demo.repository.TrainDataRepository;
import com.example.demo.store.OpenAIStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.ResponseFormat;
import io.github.sashirestela.openai.common.ResponseFormat.JsonSchema;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import java.util.List;



@Service
public class OpenAiDataService {
	
   @Autowired
    private TrainDataRepository trainDataRepository;

   @Autowired 
   private final OpenAIStore openAiStore;

   
   public OpenAiDataService(OpenAIStore openAiStore) {
       this.openAiStore = openAiStore;
   }
    
   
    public String generateData(Schema schema, String type) {
    	
    	String dataPrompt = getPrompt(schema.getSchema(), type);
    	
    	Chat chatResponse = getResponse(dataPrompt);
    	
    	saveData(chatResponse, schema.getId(), schema.getSchema());
    	
    	return "";

    }
    
    public void saveData(Chat chatResponse, String schemaId, String schema){
    	
    	if (!chatResponse.firstMessage().getContent().isBlank()) {
    		JSONObject jsonResponse = new JSONObject(chatResponse.firstMessage().getContent().toString());
            JSONArray messages = jsonResponse.getJSONArray("messages");
            
            for (int i=0; i < messages.length(); i+=2) {
            	
            	
            
            	
            	JSONArray messagesArray = new JSONArray();
            	
            	JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", "Jaarus is SQL Parser who learns schema and generates only PostgreSQL query");
                
                
                JSONObject obj = new JSONObject(messages.get(i).toString());
                
                var userMessage = """
                		%s 
                		\n
                		%s
                		""".formatted(schema, obj.get("content"));
                
                JSONObject userPrompt = new JSONObject();
                userPrompt.put("role", "user");
                userPrompt.put("content", userMessage);
                
                
                messagesArray.put(systemMessage).put(userPrompt).put(messages.get(i+1));
                
                JSONObject dataPoint = new JSONObject();
                dataPoint.put("messages", messagesArray);
                
                 
                TrainData trainData = new TrainData(schemaId, dataPoint.toString());
               
                trainDataRepository.save(trainData);
            
            }
            
    	}
    } 
    
    
    public Chat getResponse(String dataPrompt) {
    	
    	var chatRequest = ChatRequest.builder()
    			.model("gpt-4o-2024-08-06")
    			.message(SystemMessage.of("You are helpful data generator. When Schema is given you will generate english sentence and sql query which will get the answer of english senetnce"))
    		    .message(UserMessage.of(dataPrompt))
    		    .responseFormat(ResponseFormat.jsonSchema(JsonSchema.builder()
                        .name("QueryMessage")
                        .schemaClass(QueryMessage.class)
                        .build()))
                .build();
    	
    	SimpleOpenAI openAI = openAiStore.getOpenAI();
    	
    	Chat chatResponse = openAI.chatCompletions().create(chatRequest).join();
    	
    	return chatResponse;
    	
    }
    
    public String getPrompt(String schema, String type) {
    	
    	return """
	        %s
	
	        Look at the above schema and generate me 10 examples as given below:
	        Example 1 :- [
	            {
	                "role": "user",
	                "content": "List all products sold with their quantities and total revenue."
	            },
	            {
	                "role": "assistant",
	                "content": "SELECT ProductName, SUM(ofSalesUnitSold) AS TotalQuantity, SUM(ValueofSalesUnit * ofSalesUnitSold) AS TotalRevenue FROM Orders_December GROUP BY ProductName;"
	            }
	        ]
	        Generate exactly 20 examples as shown above. Generate examples which contains %s query .Return me the response in JSON format. Each example must have both roles 'user' and 'assistant'.
	    """.formatted(schema, type);
    }
    
    public static class QueryMessage {
        public List<Message> messages;

        public static class Message {
            public String role;
            public String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }
}

