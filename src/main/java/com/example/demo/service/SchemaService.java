package com.example.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.demo.model.Schema;
import com.example.demo.repository.SchemaRepository;
import com.example.demo.store.SchemaIdStore;

@Service
public class SchemaService {

    @Autowired
    private SchemaRepository schemaRepository;
    
    @Autowired
    private OpenAiDataService openAiDataService;
    
    @Autowired
    private SchemaIdStore schemaIdStore;
    
    private final AtomicReference<String> progressStatus = new AtomicReference<>("Starting...");
    
    @Async
    public CompletableFuture<Void> saveSchema(Schema schema) {
        // Save the schema to MongoDB
    	Schema savedSchema = schemaRepository.save(schema);
    	schemaIdStore.setSchemaId(savedSchema.getId());
    	schemaIdStore.setSchema(schema.getSchema());
    	
    	System.out.println("Schema id: "+savedSchema.getId());
    	
    	
    	try {
            updateProgress("Generating Simple queries...");
            openAiDataService.generateData(savedSchema, "Simple");
            updateProgress("Generated Simple queries.");
            updateProgress("Generating Complex queries...");
            openAiDataService.generateData(savedSchema, "Complex");
            updateProgress("Generated Complex queries.");
            updateProgress("Generating Joins queries...");
            openAiDataService.generateData(savedSchema, "Joins");
            updateProgress("Generated Joins queries.");
            updateProgress("Generating Window Functions queries...");
            openAiDataService.generateData(savedSchema, "Window Functions");
            updateProgress("Generated Window Functions queries.");
            updateProgress("Generating Common table expressions queries...");
            openAiDataService.generateData(savedSchema, "Common table expressions");
            updateProgress("Generated Common table expressions queries.");
            updateProgress("Generating Aggregate functions queries...");
            openAiDataService.generateData(savedSchema, "Aggregate functions");
            updateProgress("Generated Aggregate functions queries.");
            updateProgress("Completed.");
        } catch (RuntimeException e) {
            updateProgress("Error: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    public ProgressResponse getProgressStatus() {
        String status = progressStatus.get();
        boolean completed = "Completed.".equals(status) || status.startsWith("Error");
        return new ProgressResponse(status, completed);
    }

    private void updateProgress(String status) {
        progressStatus.set(status);
    }
}