package com.example.calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
        this.commentsCache = new ConcurrentHashMap<>(loadCommentsFromResource());

        try {
            // Determine the path for saving comments
            Path userHome = Paths.get(System.getProperty("user.home"));
            Path appDir = userHome.resolve(".calendar");
            if (!Files.exists(appDir)) {
                Files.createDirectories(appDir);
            }
            this.commentsFilePath = appDir.resolve("comments.json");
            if (!Files.exists(commentsFilePath)) {
                // If the file doesn't exist, create it with the initial comments from the resource
                objectMapper.writeValue(commentsFilePath.toFile(), commentsCache);
            } else {
                // If the file exists, load it into the cache
                this.commentsCache.putAll(loadCommentsFromFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize comments file for writing", e);
        }
    }

    private Map<String, String> loadCommentsFromResource() {
        try {
            ClassPathResource resource = new ClassPathResource("comments.json");
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, String> comments = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});
                return comments != null ? new ConcurrentHashMap<>(comments) : new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Could not load comments.json from resources, starting with an empty map: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
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
