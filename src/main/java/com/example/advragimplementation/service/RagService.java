package com.example.advragimplementation.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RagService {

    String chatwithAi(String question);

    void extractPdfData(MultipartFile file) throws IOException;
}
