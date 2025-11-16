package com.nikhilpillay.aggregator.controller;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.service.TransactionClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final OllamaChatModel model;

    private final TransactionClassifierService transactionClassifierService;

    @PostMapping("/prompt")
    public String promptModel(@RequestBody String prompt) {
        ChatResponse response = model.call(new Prompt(prompt));
        return response.getResult().getOutput().getText();
    }

    @PostMapping("/classify")
    public TransactionCategory classify(@RequestBody String description) {
        return transactionClassifierService.classify(description);
    }
}
