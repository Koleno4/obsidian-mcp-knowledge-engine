package com.meek.obsidian_second_brain.search;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class NoteSearchService {
    private final VectorStore vectorStore;

    public NoteSearchService(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    /**
     * Performs semantic search on indexed Obsidian notes chunks
     * @param query The naturakl langugge prompt/question
     * @param topK the max number of relevant chunks to return
     * @return list of matching document chunks
     */
    public List<Document> search(String query, int topK){
        SearchRequest searchRequest = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .similarityThreshold(0.75)
            .build();
        return vectorStore.similaritySearch(searchRequest);
    }
}
