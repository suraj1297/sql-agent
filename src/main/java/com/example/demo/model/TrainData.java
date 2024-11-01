package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "traindata")
public class TrainData {
	
	@Id
    private String id;
	private String messages;
    private String schemaId;
    
    public TrainData(String schemaId, String messages){
    	
    	this.schemaId = schemaId;
    	this.messages = messages;
    	
    }
    
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String data) {
		this.messages = data;
	}
	public String getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	

}
