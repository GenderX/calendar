package com.example.calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentService {

    private final Path commentsFilePath;
    private final ObjectMapper objectMapper;
    private final Map<String, String> commentsCache;

    public CommentService() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            // Resolve the path within the project structure, suitable for development
            this.commentsFilePath = Paths.get("src", "main", "resources", "comments.json");
            if (!Files.exists(commentsFilePath)) {
                Files.write(commentsFilePath, "{}".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize comments file", e);
        }
        this.commentsCache = new ConcurrentHashMap<>(loadCommentsFromFile());
    }

    private Map<String, String> loadCommentsFromFile() {
        if (!Files.exists(commentsFilePath)) {
            return new ConcurrentHashMap<>();
        }
        try (InputStream inputStream = Files.newInputStream(commentsFilePath)) {
            Map<String, String> comments = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});
            return comments != null ? comments : new ConcurrentHashMap<>();
        } catch (IOException e) {
            System.err.println("Error loading comments, starting with an empty map: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }

    public Map<String, String> getAllComments() {
        return Collections.unmodifiableMap(commentsCache);
    }

    public synchronized void saveComment(String date, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            commentsCache.remove(date);
        } else {
            commentsCache.put(date, comment);
        }
        try {
            objectMapper.writeValue(commentsFilePath.toFile(), commentsCache);
        } catch (IOException e) {
            // In case of failure, reload from the last good state
            this.commentsCache.clear();
            this.commentsCache.putAll(loadCommentsFromFile());
            throw new RuntimeException("Failed to save comment", e);
        }
    }
}
