package com.nikhilpillay.aggregator.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final OllamaChatModel model;

    public ChatController(OllamaChatModel model) {
        this.model = model;
    }

    @PostMapping("/prompt")
    public String promptModel(@RequestBody String prompt) {
        ChatResponse response = model.call(new Prompt(prompt));
        return response.getResult().getOutput().getText();
    }
}
