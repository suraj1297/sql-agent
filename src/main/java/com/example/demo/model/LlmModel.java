package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "models")
public class LlmModel {
	
	@Id
	private String id;
	private String schemId;
	private String fineTuningId;
	private String fineTunedModel;
	
	public LlmModel(String id, String schemaId, String fineTuningId, String fineTunedModel) {
		this.id = id;
		this.schemId = schemaId;
		this.fineTuningId = fineTuningId;
		this.fineTunedModel = fineTunedModel;
		
	}
	
	
	public LlmModel() {
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSchemId() {
		return schemId;
	}
	public void setSchemId(String schemId) {
		this.schemId = schemId;
	}
	public String getFineTuningId() {
		return fineTuningId;
	}
	public void setFineTuningId(String fineTuningId) {
		this.fineTuningId = fineTuningId;
	}
	public String getFineTunedModel() {
		return fineTunedModel;
	}
	public void setFineTunedModel(String fineTunedModel) {
		this.fineTunedModel = fineTunedModel;
	}
	
	
	
}
