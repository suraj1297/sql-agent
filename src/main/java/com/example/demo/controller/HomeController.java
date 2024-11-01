package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.Chat;
import com.example.demo.service.FineTuneLLM;
import com.example.demo.service.ProgressResponse;
import com.example.demo.service.SchemaService;
import com.example.demo.store.SchemaIdStore;
import com.example.demo.model.Schema;


@Controller
public class HomeController {

    @Autowired
    private SchemaService schemaService;
    
    @Autowired
    private SchemaIdStore schemaIdStore;
    
    @Autowired
    private FineTuneLLM fineTuneLLM;
	
	@Autowired
	private Chat chat;
    

    @PostMapping("/submit")
    @ResponseBody
    public String handleSubmit(@RequestParam("schema") String schema) {
        Schema usrSchema = new Schema(schema);
        schemaService.saveSchema(usrSchema);
        return "Processing started";
    }

    @GetMapping("/progress")
    public String showProgressPage(Model model) {
        model.addAttribute("response", "Generating data Tailored for your Schema");
        return "progress";
    }

    @GetMapping("/progress-status")
    @ResponseBody
    public ProgressResponse getProgressStatus() {
        return schemaService.getProgressStatus();
    }
    
    @GetMapping("/finetune")
    public String finetune(Model model) {
    	
    	
    	fineTuneLLM.aggregateAndExportToJsonl(schemaIdStore.getSchemaId());
    	model.addAttribute("aggregateStatus", "Data File Created");
    	
    	fineTuneLLM.fineTune(schemaIdStore.getSchemaId());
    	model.addAttribute("fineTuneStatus", "Started Fine tuning model");
    	System.out.println("Started Fine tuning model: "+ fineTuneLLM.fineTuningId);
    	
    	fineTuneLLM.fineTuneStatus(schemaIdStore.getSchemaId());
    	model.addAttribute("fineTuneCompleteStatus", "Successfully Fine tuned model \n " + fineTuneLLM.fineTunedModel);
    	
    	model.addAttribute("modelID", fineTuneLLM.fineTuningId);
    	model.addAttribute("modelName", fineTuneLLM.fineTunedModel);
    	model.addAttribute("schemaId", schemaIdStore.getSchemaId());
    	
    	System.out.println("Successfully Fine tuned model: "+ fineTuneLLM.fineTunedModel);
    	
    	
        return "finetuning";
    }
    
      @GetMapping("/chat")
	  public String handleChatRedirect(@RequestParam("modelId") String modelId, 
			  						   @RequestParam("modelName") String modelName, 
	                                   @RequestParam("schemaId") String schemaId, 
	                                   Model model) {
    	  
	      model.addAttribute("modelName", modelName);
	      model.addAttribute("schemaId", schemaId);
	     
	      
	      return "chat"; 
	  }
      
      @PostMapping("/chat/send")
      @ResponseBody
      public String handleChatMessage(@RequestParam("message") String message, @RequestParam("modelName") String modelName, @RequestParam("schemaId") String schemaId) {
		return chat.getResponse(message, modelName, schemaId);
      }

    
}

