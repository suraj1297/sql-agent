package com.example.demo.store;

import org.springframework.stereotype.Component;

@Component
public class SchemaIdStore {
	
	private String schemaId;
	private String schema;

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
