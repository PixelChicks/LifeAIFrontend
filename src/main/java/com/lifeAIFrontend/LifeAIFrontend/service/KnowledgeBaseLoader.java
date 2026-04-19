package com.lifeAIFrontend.LifeAIFrontend.service;

import jakarta.annotation.PostConstruct;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads .docx knowledge-base files from a configurable directory.
 * Falls back to the hardcoded MedicalKnowledgeBase if no files are found.
 *
 * Place your Word files in the directory configured by:
 *   knowledge.base.directory (default: ./knowledge-base)
 */
@Service
public class KnowledgeBaseLoader {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseLoader.class);

    @Value("${knowledge.base.directory:./knowledge-base}")
    private String knowledgeBaseDirectory;

    private String combinedKnowledge;

    @PostConstruct
    public void loadKnowledgeBase() {
        List<String> documents = new ArrayList<>();
        Path dir = Paths.get(knowledgeBaseDirectory);

        if (Files.exists(dir) && Files.isDirectory(dir)) {
            File[] docxFiles = dir.toFile().listFiles(
                    f -> f.isFile() && f.getName().toLowerCase().endsWith(".docx")
            );

            if (docxFiles != null && docxFiles.length > 0) {
                for (File docx : docxFiles) {
                    try {
                        String content = extractTextFromDocx(docx);
                        if (!content.isBlank()) {
                            documents.add("=== " + docx.getName() + " ===\n" + content);
                            log.info("Loaded knowledge base file: {}", docx.getName());
                        }
                    } catch (Exception e) {
                        log.warn("Could not read knowledge base file {}: {}", docx.getName(), e.getMessage());
                    }
                }
            }
        }

        if (documents.isEmpty()) {
            log.info("No .docx knowledge base files found in '{}'. Using built-in knowledge base.", knowledgeBaseDirectory);
            combinedKnowledge = MedicalKnowledgeBase.KNOWLEDGE_BASE;
        } else {
            // Include both the Word file content AND the built-in base
            combinedKnowledge = MedicalKnowledgeBase.KNOWLEDGE_BASE
                    + "\n\n--- ДОПЪЛНИТЕЛНИ ДОКУМЕНТИ ---\n\n"
                    + String.join("\n\n", documents);
        }
    }

    private String extractTextFromDocx(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument doc = new XWPFDocument(fis)) {

            return doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .filter(t -> !t.isBlank())
                    .collect(Collectors.joining("\n"));
        }
    }

    public String getCombinedKnowledge() {
        return combinedKnowledge;
    }

    /**
     * Call this to reload knowledge base files at runtime (e.g. after uploading new files).
     */
    public void reload() {
        loadKnowledgeBase();
    }
}