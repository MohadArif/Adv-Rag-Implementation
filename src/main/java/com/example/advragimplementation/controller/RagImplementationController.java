package com.example.advragimplementation.controller;

import com.example.advragimplementation.service.RagImplementationServiceImpl;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class RagImplementationController {


    private final RagImplementationServiceImpl ragImplementationService;

    public RagImplementationController(RagImplementationServiceImpl ragImplementationService) {
        this.ragImplementationService = ragImplementationService;
    }

    @GetMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public String getAiResponse(@RequestParam("question") String question){
        return ragImplementationService.chatwithAi(question);
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public String loadPdfData(@RequestBody MultipartFile file) throws IOException {
        ragImplementationService.extractPdfData(file);
        return "data save to database";
    }
}
