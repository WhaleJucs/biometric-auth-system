package br.edu.biometric.repository;

import br.edu.biometric.model.AccessLog;
import br.edu.biometric.model.AccessStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositório para gerenciar logs de acesso em arquivo JSON
 */
public class AccessLogRepository {

    private static final String DATA_DIR = "data";
    private static final String LOGS_FILE = DATA_DIR + "/access_logs.json";

    private final Gson gson;
    private List<AccessLog> logs;

    public AccessLogRepository() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        initializeDataDirectory();
        loadLogs();
    }

    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretórios: " + e.getMessage());
        }
    }

    private void loadLogs() {
        File file = new File(LOGS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<AccessLog>>(){}.getType();
                logs = gson.fromJson(reader, listType);
                if (logs == null) {
                    logs = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar logs: " + e.getMessage());
                logs = new ArrayList<>();
            }
        } else {
            logs = new ArrayList<>();
        }
    }

    private void saveLogs() {
        try (Writer writer = new FileWriter(LOGS_FILE)) {
            gson.toJson(logs, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar logs: " + e.getMessage());
        }
    }

    public AccessLog save(AccessLog log) {
        logs.add(log);
        saveLogs();
        return log;
    }

    public List<AccessLog> findAll() {
        return new ArrayList<>(logs);
    }

    public List<AccessLog> findByUserId(String userId) {
        return logs.stream()
                .filter(log -> userId.equals(log.getUserId()))
                .sorted(Comparator.comparing(AccessLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AccessLog> findByStatus(AccessStatus status) {
        return logs.stream()
                .filter(log -> log.getStatus() == status)
                .sorted(Comparator.comparing(AccessLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AccessLog> findRecent(int limit) {
        return logs.stream()
                .sorted(Comparator.comparing(AccessLog::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<AccessLog> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return logs.stream()
                .filter(log -> !log.getTimestamp().isBefore(start) && 
                              !log.getTimestamp().isAfter(end))
                .sorted(Comparator.comparing(AccessLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public long countByStatus(AccessStatus status) {
        return logs.stream()
                .filter(log -> log.getStatus() == status)
                .count();
    }

    public long countSuccessful() {
        return countByStatus(AccessStatus.SUCCESS);
    }

    public long countFailed() {
        return logs.stream()
                .filter(log -> !log.getStatus().isSuccess())
                .count();
    }

    public long count() {
        return logs.size();
    }

    public void clear() {
        logs.clear();
        saveLogs();
    }
}

