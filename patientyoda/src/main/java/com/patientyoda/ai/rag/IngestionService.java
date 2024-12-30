package com.patientyoda.ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class IngestionService implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    @Value("classpath:/docs/AsthmaManagementGuidelinesReport-2-4-21.pdf")
    private Resource asthamaPDF;

    @Value("classpath:/docs/PIIS0190962223028785.pdf")
    private Resource piiPDF;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {


        TextSplitter textSplitter = new TokenTextSplitter();

        var asthmaReader = new ParagraphPdfDocumentReader(asthamaPDF);
        vectorStore.accept(textSplitter.apply(asthmaReader.get()));


        var piiReader = new ParagraphPdfDocumentReader(piiPDF);
        vectorStore.accept(textSplitter.apply(piiReader.get()));

        LOGGER.info("VectorStore Loaded with data!");


    }
}