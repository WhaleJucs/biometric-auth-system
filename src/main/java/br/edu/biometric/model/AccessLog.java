package br.edu.biometric.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Classe que representa um log de tentativa de acesso ao sistema
 */
public class AccessLog {

    private String id;
    private String userId;
    private String userName;
    private AccessLevel accessLevel;
    private LocalDateTime timestamp;
    private AccessStatus status;
    private String details;
    private double confidenceScore; // Score de confiança do reconhecimento facial (0-100)

    public AccessLog() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public AccessLog(String userId, String userName, AccessLevel accessLevel, 
                     AccessStatus status, String details, double confidenceScore) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.accessLevel = accessLevel;
        this.status = status;
        this.details = details;
        this.confidenceScore = confidenceScore;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public AccessStatus getStatus() {
        return status;
    }

    public void setStatus(AccessStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s - %s (%.2f%%) - %s",
                getFormattedTimestamp(),
                userName,
                accessLevel != null ? accessLevel.getDisplayName() : "N/A",
                status,
                confidenceScore,
                details);
    }
}

