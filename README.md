# obsidian-mcp-knowledge-engine
A high-performance Spring AI &amp; Model Context Protocol (MCP) server providing local RAG and semantic search over Obsidian Markdown vaults for Claude Desktop


com.example.obsidianmcp/
│
├── ObsidianSecondBrainApplication.java   # Hauptklasse (Spring Boot)
│
├── config/                               # Konfigurationsklassen (Beans, Custom Mappings)
│
├── ingestion/                            # Dateiverarbeitung & Text-Säuberung
│   └── VaultIngestionService.java        # Dein eben erstellter Service
│
├── search/                               # Vektordatenbank & RAG-Logik (Phase 3)
│   └── NoteSearchService.java
│
└── mcp/                                  # MCP-Tools & Claude-Schnittstellen (Phase 4)
    ├── ObsidianMcpTools.java
    └── NoteChunkDto.java


### Project Structure

`../config` : Configuration classes : Beans, Custom Mappings <br>
`../ingestion` : Data processing, text cleaning <br>
`search` : Vector databse & RAG logic <br>
`mcp` : MCP Tools and Claude interface

