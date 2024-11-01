package com.example.demo.service;

import java.nio.file.Paths;
import org.springframework.stereotype.Service;

import com.example.demo.store.OpenAIStore;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileRequest.PurposeType;
import io.github.sashirestela.openai.domain.file.FileResponse;

@Service
public class OpenAiFile {
	
	private SimpleOpenAI openAI;
	

    
    public OpenAiFile(OpenAIStore openAiStore) {
       this.openAI = openAiStore.getOpenAI();
    }
	
	public FileResponse createFile(String filePath, PurposeType purpose) {
		
        var fileRequest = FileRequest.builder()
                .file(Paths.get(filePath))
                .purpose(purpose)
                .build();
        
        
        
        var futureFile = openAI.files().create(fileRequest);
        return futureFile.join();
    }
	
	@SuppressWarnings("deprecation")
    public void waitUntilFileIsProcessed(String fileId) {
        FileResponse fileResponse = null;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            fileResponse = openAI.files().getOne(fileId).join();
        } while (!fileResponse.getStatus().equals("processed"));
    }

}
