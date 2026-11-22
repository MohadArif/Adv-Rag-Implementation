package com.example.advragimplementation.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RagImplementationServiceImpl implements RagService {


    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagImplementationServiceImpl(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String chatwithAi(String question) {



        /*
         * RetrievalAugmentationAdvisor:
         * Responsible for managing the Advanced RAG retrieval pipeline.
         * It combines query transformation and document retrieval.
         */
//        RetrievalAugmentationAdvisor augmentationAdvisor = RetrievalAugmentationAdvisor.builder()
        /*
         * queryTransformers():
         * Transforms the original user query before searching
         * the vector database.
         *
         * TranslationQueryTransformer:
         * Translates the user's question into English.
         *
         * Example:
         * User Query: "मुझे Java के बारे में बताओ"
         * Transformed Query: "Tell me about Java"
         *
         * chatClientBuilder():
         * Provides the ChatClient builder used by the
         * transformer to perform translation using an LLM.
         *
         * targetLanguage("english"):
         * Specifies English as the target language.
         */
//        .queryTransformers(
//                TranslationQueryTransformer.builder().chatClientBuilder(this.chatClient.mutate().clone())
//                        .targetLanguage("english")
//                        .build()
//        )
        /*
         * documentRetriever():
         * Defines how relevant documents will be retrieved
         * from the vector database.
         *
         * VectorStoreDocumentRetriever:
         * Searches the vector database using semantic similarity.
         *
         * vectorStore():
         * Specifies the vector database where document embeddings
         * are stored.
         *
         * similarityThreshold(0.5):
         * Retrieves documents whose similarity score satisfies
         * the configured threshold.
         *
         * topK(3):
         * Limits retrieval to a maximum of 3 relevant documents.
         */
//        .documentRetriever(VectorStoreDocumentRetriever.builder()
//                        .vectorStore(vectorStore)
//                        .similarityThreshold(0.5)
//                        .topK(3)
//                .build())
                    // Builds the complete RAG advisor.
//                .build();





        /*
         * RetrievalAugmentationAdvisor:
         * Orchestrates the Advanced RAG pipeline.
         * It manages query transformation and document retrieval
         * before sending relevant context to the LLM.
         */
        RetrievalAugmentationAdvisor rewriteTransformer =
                RetrievalAugmentationAdvisor.builder()

                        /*
                         * queryTransformers():
                         * Transforms the original user query into a better
                         * search query before retrieving documents.
                         *
                         * RewriteQueryTransformer:
                         * Uses an LLM to rewrite the user's question
                         * into a more meaningful and search-friendly query.
                         *
                         * Example:
                         * Original Query:
                         * "What about its features?"
                         *
                         * Rewritten Query:
                         * "What are the features of Java?"
                         *
                         * chatClientBuilder():
                         * Provides ChatClient to the transformer so that
                         * it can use an LLM for query rewriting.
                         *
                         * chatClient.mutate().clone():
                         * Creates a separate ChatClient builder configuration
                         * for the transformer.
                         */
                        .queryTransformers(
                                RewriteQueryTransformer.builder()
                                        .chatClientBuilder(chatClient.mutate().clone())
                                        .build()
                        )

                        /*
                         * documentRetriever():
                         * Configures the document retrieval mechanism.
                         *
                         * VectorStoreDocumentRetriever:
                         * Searches the vector database to find documents
                         * semantically similar to the rewritten query.
                         *
                         * vectorStore():
                         * Specifies the vector database containing
                         * document embeddings and metadata.
                         *
                         * similarityThreshold(0.5):
                         * Sets the minimum similarity threshold.
                         * Documents that do not satisfy this threshold
                         * are filtered out.
                         *
                         * topK(3):
                         * Retrieves a maximum of 3 relevant documents.
                         */
                        .documentRetriever(
                                VectorStoreDocumentRetriever.builder()
                                        .vectorStore(vectorStore)
                                        .similarityThreshold(0.5)
                                        .topK(3)
                                        .build()
                        )

                        /*
                         * build():
                         * Creates the final RetrievalAugmentationAdvisor
                         * with the configured query transformer
                         * and document retriever.
                         */
                        .build();




        return chatClient.prompt()
                .advisors(rewriteTransformer)
                .user(question)
                .call()
                .content();
    }

    @Override
    public void extractPdfData(MultipartFile file) throws IOException {

        try (PDDocument loadedPDF = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String textData = pdfTextStripper.getText(loadedPDF);
            Document document = new Document(textData);  //1st way
            vectorStore.add(List.of(document));
        }
    }
}
