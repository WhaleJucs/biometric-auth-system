package br.edu.biometric.service;

import br.edu.biometric.model.AccessStatus;
import br.edu.biometric.model.User;

/**
 * Classe que encapsula o resultado de uma tentativa de autenticação
 */
public class AuthenticationResult {

    private boolean success;
    private User user;
    private AccessStatus status;
    private String message;
    private double confidence; // 0-100%

    public AuthenticationResult() {
        this.success = false;
        this.confidence = 0.0;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccessStatus getStatus() {
        return status;
    }

    public void setStatus(AccessStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

