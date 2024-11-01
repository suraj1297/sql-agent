package com.example.demo.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.LlmModel;
import com.example.demo.model.TrainData;
import com.example.demo.repository.LlmModelRepository;
import com.example.demo.repository.TrainDataRepository;
import com.example.demo.store.OpenAIStore;

import io.github.sashirestela.openai.domain.file.FileRequest.PurposeType;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.file.FileResponse;
import io.github.sashirestela.openai.domain.finetuning.FineTuning;
import io.github.sashirestela.openai.domain.finetuning.FineTuningRequest;
import io.github.sashirestela.openai.domain.finetuning.HyperParams;

@Service
public class FineTuneLLM {


    @Autowired
    private TrainDataRepository trainDataRepository;

    @Autowired
    private LlmModelRepository llmModelRepository;
    
    @Autowired
	private OpenAiFile openAiFile;

    private SimpleOpenAI openAI;
    private String fileId;
    public String fineTuningId;
    public String fineTunedModel;
    
    
    public FineTuneLLM(OpenAIStore openAiStore) {
    	this.openAI = openAiStore.getOpenAI();
    }

    public void aggregateAndExportToJsonl(String schemaId) {
        String filePath = "train_data_" + schemaId + ".jsonl";
        List<TrainData> trainDataList = trainDataRepository.findBySchemaId(schemaId);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (TrainData data : trainDataList) {
                fileWriter.write(data.getMessages().toString() + "\n");
            }
            System.out.println("Data successfully aggregated and exported to train_data_" + schemaId + ".jsonl");
        } catch (IOException e) {
            System.err.println("Error writing to JSONL file: " + e.getMessage());
            return;
        }

        uploadFile(filePath);
    }

    public void uploadFile(String filePath) {
    	
        File file = new File(filePath);
        if (file.exists()) {
            FileResponse fileResponse = openAiFile.createFile(filePath, PurposeType.FINE_TUNE);
            if (fileResponse != null) {
                this.fileId = fileResponse.getId();
                System.out.println("File successfully uploaded with ID: " + fileId);
            } else {
                System.err.println("Error: File response is null");
            }
        } else {
            System.err.println("Error: File not found at " + filePath);
        }
    }

    public void fineTune(String schemaId) {
        if (fileId == null) {
            System.err.println("Error: Training file ID is not set");
            return;
        }

        FineTuningRequest fineTuningRequest = FineTuningRequest.builder()
                .trainingFile(fileId)
                .model("gpt-4o-mini-2024-07-18")
                .hyperparameters(HyperParams.builder()
                        .batchSize("auto")
                        .learningRateMultiplier("auto")
                        .nEpochs("3")
                        .build())
                .suffix("sql-agent-" + schemaId)
                .seed(99)
                .build();

        var futureFineTuning = openAI.fineTunings().create(fineTuningRequest);
        var fineTuningResponse = futureFineTuning.join();

        if (fineTuningResponse != null) {
            this.fineTuningId = fineTuningResponse.getId();
            System.out.println("Fine Tuning initiated: " + fineTuningId);
        } else {
            System.err.println("Error: Fine Tuning response is null");
        }
    }

    public void fineTuneStatus(String schemaId) {
        if (fineTuningId == null) {
            System.err.println("Error: Fine Tuning ID is not set");
            return;
        }

        FineTuning fineTuningResponse;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            var futureFineTuning = openAI.fineTunings().getOne(this.fineTuningId);
            fineTuningResponse = futureFineTuning.join();
        } while (!"succeeded".equals(fineTuningResponse.getStatus()));

        this.fineTunedModel = fineTuningResponse.getFineTunedModel();

        LlmModel llmModel = new LlmModel();
        llmModel.setSchemId(schemaId);
        llmModel.setFineTuningId(this.fineTuningId);
        llmModel.setFineTunedModel(this.fineTunedModel);

        llmModelRepository.save(llmModel);
        System.out.println("Successfully saved LLM model details to repository.");
    }
}
