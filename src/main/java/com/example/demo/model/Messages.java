package com.example.demo.model;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;

import java.util.ArrayList;

@Component
public class Messages {
	
	
	private List<ChatMessage> messages;
	
	public Messages() {
	
		messages = new ArrayList<>();
		messages.add(SystemMessage.of("Jaarus is SQL Parser who learns schema and generates MySQL query"));
	
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
