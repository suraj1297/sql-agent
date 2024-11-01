package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;

@Component
public class SummarizerMessages {
	
	private List<ChatMessage> messages;
	
	public SummarizerMessages() {
	
		messages = new ArrayList<>();
		messages.add(SystemMessage.of("You are execellent in understanding user requirements. User is interacting with sql db and asking question. So you have acess to user questions and realted db data."
				+ "You will understand what exactly user is asking for include and will include only relevant columns and summarize the db results in easy and precise natural language so that user can "
				+ "understand the tabular data."));
	
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(String myMessage, Boolean user) {
		if (user)
			this.messages.add(UserMessage.of(myMessage));
		else {
	
			this.messages.add(AssistantMessage.of(myMessage));
		}
	}


}
