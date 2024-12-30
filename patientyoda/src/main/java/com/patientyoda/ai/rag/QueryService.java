package com.patientyoda.ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);
    private final VectorStore vectorStore;

    public QueryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String query(String query) {

        LOGGER.debug("## Querying : {}" , query);

        SearchRequest searchRequest =
                SearchRequest.query(query)
                        .withSimilarityThreshold(0.7d)
                        .withTopK(3);

        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        if (similarDocs.isEmpty()) {
            LOGGER.info("No documents found for query: " + query);
            return "";
        }


        String mergedDocs = similarDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        LOGGER.debug("Merged docs: {}" , mergedDocs);

        return mergedDocs;


    }
}
