package br.edu.biometric.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe que representa um usuário do sistema
 */
public class User {

    private String id;
    private String name;
    private String cpf;
    private String email;
    private AccessLevel accessLevel;
    private List<String> biometricDataPaths; // Caminhos das imagens faciais
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.biometricDataPaths = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    public User(String name, String cpf, String email, AccessLevel accessLevel) {
        this();
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.accessLevel = accessLevel;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getBiometricDataPaths() {
        return biometricDataPaths;
    }

    public void setBiometricDataPaths(List<String> biometricDataPaths) {
        this.biometricDataPaths = biometricDataPaths;
        this.updatedAt = LocalDateTime.now();
    }

    public void addBiometricData(String path) {
        this.biometricDataPaths.add(path);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", accessLevel=" + accessLevel +
                ", active=" + active +
                '}';
    }
}

