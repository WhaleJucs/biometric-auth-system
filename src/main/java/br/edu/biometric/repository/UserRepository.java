package br.edu.biometric.repository;

import br.edu.biometric.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositório para gerenciar a persistência de usuários em arquivo JSON
 */
public class UserRepository {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.json";

    private final Gson gson;
    private List<User> users;

    public UserRepository() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        initializeDataDirectory();
        loadUsers();
    }

    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(DATA_DIR + "/biometric"));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretórios: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                users = gson.fromJson(reader, listType);
                if (users == null) {
                    users = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar usuários: " + e.getMessage());
                users = new ArrayList<>();
            }
        } else {
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    public User save(User user) {
        Optional<User> existing = findById(user.getId());
        if (existing.isPresent()) {
            users.remove(existing.get());
        }
        users.add(user);
        saveUsers();
        return user;
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByCpf(String cpf) {
        return users.stream()
                .filter(u -> u.getCpf().equals(cpf))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public List<User> findAllActive() {
        return users.stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }

    public boolean delete(String id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    public long count() {
        return users.size();
    }

    public long countActive() {
        return users.stream().filter(User::isActive).count();
    }
}

