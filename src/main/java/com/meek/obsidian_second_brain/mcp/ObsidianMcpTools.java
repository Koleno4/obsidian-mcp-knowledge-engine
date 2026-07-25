package com.meek.obsidian_second_brain.mcp;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.meek.obsidian_second_brain.search.NoteSearchService;

@Component
public class ObsidianMcpTools {
    private final NoteSearchService noteSearchService;

    public ObsidianMcpTools(NoteSearchService noteSearchService){
        this.noteSearchService = noteSearchService;
    }

    @Tool(
        name = "search_obsidian_notes",
        description = "Performs a semantic similarity search across the user's Obsidian Second Brain notes and returns matching content snippets."
    )
    public String searchObsidianNotes(String query){
        List<Document> results = noteSearchService.search(query, 5);

        if (results.isEmpty()){
            return "No relevant notes found in your Obsidian vault";
        }

        return results.stream()
                .map(doc -> String.format(
                        "--- Note Source: %s ---\n%s\n",
                        doc.getMetadata().getOrDefault("file_name", "Unknown"),
                        doc.getFormattedContent()
                ))
                .collect(Collectors.joining("\n"));
    }
}
