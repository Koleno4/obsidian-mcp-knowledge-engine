package com.meek.obsidian_second_brain.ingestion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class VaultIngestionService {
    private static final Logger log = LoggerFactory.getLogger(VaultIngestionService.class);

    @Value("${obsidian.vault-path:}")
    private String vaultPath; // injects vault path

    // separates long texts in consumable chunks
    private final TokenTextSplitter textSplitter = TokenTextSplitter.builder().build();
    // Vector DB to store the vault semantic content
    private final VectorStore vectorStore;

    public VaultIngestionService(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    /**
     * Called automatically after start of spring app 
     */
    @EventListener(ApplicationReadyEvent.class)
    public void ingestOnStartup() {
        log.info("Start injestion for obsidian vault: {}", vaultPath);

        Path rootPath = Paths.get(vaultPath);

        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)){
            log.error("The given vault path does not exist: {}", vaultPath);
            return;
        }

        List<Document> allChunks = scanAndProcessVault(rootPath);
        log.info("Injection successful. Total generated chunks: {}", allChunks.size());

        // save chunks in VectorDB
        if (!allChunks.isEmpty()){
            log.info("Storing chunks in Vector Store...");
            vectorStore.add(allChunks);
            log.info("Successfully indexed {} chunks in Vector Store!", allChunks.size());
        }
    }

    private List<Document> scanAndProcessVault(Path rootPath) {
        List<Document> processedChunks = new ArrayList<>();

        try(Stream<Path> paths = Files.walk(rootPath)){
            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".md")) // only md files
                .forEach(path -> {
                        try {
                            String rawContent = Files.readString(path);
                            String cleanedContent = sanitizeObsidianMarkdown(rawContent);

                            if (!cleanedContent.isBlank()){
                                // metadata of the file. auxilliary to check original real file to source
                                Map<String, Object> metadata = Map.of(
                                    "file_name", path.getFileName().toString(), 
                                    "file_path", path.toAbsolutePath().toString()
                                );

                                Document rawDoc = new Document(cleanedContent, metadata);
                                List<Document> chunks = textSplitter.apply(List.of(rawDoc));
                                processedChunks.addAll(chunks);

                            }
                        } catch (IOException e){
                            log.error("Error on reading file: {}", path, e);
                        }
                    }
                );


        } catch (IOException e){
            log.error("Error on scanning the vault dir", e);
        }
        return processedChunks;
    }

    private String sanitizeObsidianMarkdown(String content) {
        if (content == null) return "";

        // 1. YAML Frontmatter entfernen (Header oben in den Notizen)
        String noFrontmatter = content.replaceAll("(?s)^---.*?---\\s*", "");

        // 2. Wikilinks [[Notiz Name]] zu "Notiz Name" umwandeln
        String noWikilinks = noFrontmatter.replaceAll("\\[\\[(.*?)\\]\\]", "$1");

        return noWikilinks.trim();
    }
}
