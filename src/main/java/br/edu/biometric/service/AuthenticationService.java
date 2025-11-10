package br.edu.biometric.service;

import br.edu.biometric.model.*;
import br.edu.biometric.repository.AccessLogRepository;
import br.edu.biometric.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço responsável pela autenticação biométrica e controle de acesso
 */
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final AccessLogRepository logRepository;
    private final FacialRecognitionService faceRecognitionService;
    private final Map<String, Integer> userLabelMap; // Mapeia userId para label numérico
    private int nextLabel = 1;
    private boolean modelTrained = false;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
        this.logRepository = new AccessLogRepository();
        this.faceRecognitionService = new FacialRecognitionService();
        this.userLabelMap = new HashMap<>();
        trainModel();
    }

    /**
     * Treina o modelo de reconhecimento facial com todos os usuários cadastrados
     */
    public void trainModel() {
        if (!faceRecognitionService.isInitialized()) {
            System.err.println("Serviço de reconhecimento facial não está disponível!");
            return;
        }

        List<User> users = userRepository.findAllActive();
        if (users.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado para treinamento.");
            return;
        }

        userLabelMap.clear();
        nextLabel = 1;

        for (User user : users) {
            if (!user.getBiometricDataPaths().isEmpty()) {
                int label = nextLabel++;
                userLabelMap.put(user.getId(), label);
                faceRecognitionService.trainRecognizer(user.getBiometricDataPaths(), label);
            }
        }

        modelTrained = !userLabelMap.isEmpty();
        System.out.println("Modelo treinado com " + userLabelMap.size() + " usuários.");
    }

    /**
     * Autentica um usuário através de reconhecimento facial
     * @param imagePath Caminho da imagem para autenticação
     * @param requiredLevel Nível de acesso requerido
     * @return Resultado da autenticação
     */
    public AuthenticationResult authenticate(String imagePath, AccessLevel requiredLevel) {
        AuthenticationResult result = new AuthenticationResult();

        // Verifica se o serviço está disponível
        if (!faceRecognitionService.isInitialized()) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.ERROR);
            result.setMessage("Serviço de reconhecimento facial não disponível.");
            logAccess(null, requiredLevel, result);
            return result;
        }

        // Verifica se há modelo treinado
        if (!modelTrained || userLabelMap.isEmpty()) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.ERROR);
            result.setMessage("Nenhum usuário cadastrado no sistema.");
            logAccess(null, requiredLevel, result);
            return result;
        }

        // Tenta reconhecer a face
        int[] recognition = faceRecognitionService.recognizeFace(imagePath);
        
        if (recognition == null) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_NOT_RECOGNIZED);
            result.setMessage("Nenhuma face detectada ou reconhecida.");
            result.setConfidence(0.0);
            logAccess(null, requiredLevel, result);
            return result;
        }

        int label = recognition[0];
        int confidenceValue = recognition[1];
        double confidencePercentage = faceRecognitionService.getConfidencePercentage(confidenceValue);
        
        result.setConfidence(confidencePercentage);

        // Verifica a confiança do reconhecimento
        if (!faceRecognitionService.isConfidenceAcceptable(confidenceValue)) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_LOW_CONFIDENCE);
            result.setMessage(String.format("Confiança insuficiente: %.2f%%", confidencePercentage));
            logAccess(null, requiredLevel, result);
            return result;
        }

        // Busca o usuário pelo label
        String userId = getUserIdByLabel(label);
        if (userId == null) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_NOT_RECOGNIZED);
            result.setMessage("Usuário não encontrado.");
            logAccess(null, requiredLevel, result);
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_NOT_RECOGNIZED);
            result.setMessage("Usuário não encontrado.");
            logAccess(null, requiredLevel, result);
            return result;
        }

        User user = userOpt.get();
        result.setUser(user);

        // Verifica se o usuário está ativo
        if (!user.isActive()) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_INACTIVE_USER);
            result.setMessage("Usuário inativo.");
            logAccess(user, requiredLevel, result);
            return result;
        }

        // Verifica o nível de acesso
        if (!user.getAccessLevel().canAccess(requiredLevel)) {
            result.setSuccess(false);
            result.setStatus(AccessStatus.DENIED_INSUFFICIENT_PERMISSION);
            result.setMessage(String.format("Acesso negado. Requer: %s, Possui: %s", 
                    requiredLevel.getDisplayName(), 
                    user.getAccessLevel().getDisplayName()));
            logAccess(user, requiredLevel, result);
            return result;
        }

        // Autenticação bem-sucedida
        result.setSuccess(true);
        result.setStatus(AccessStatus.SUCCESS);
        result.setMessage(String.format("Bem-vindo(a), %s! Confiança: %.2f%%", 
                user.getName(), confidencePercentage));
        logAccess(user, requiredLevel, result);

        return result;
    }

    /**
     * Registra o log de acesso
     */
    private void logAccess(User user, AccessLevel requiredLevel, AuthenticationResult result) {
        AccessLog log = new AccessLog();
        log.setUserId(user != null ? user.getId() : null);
        log.setUserName(user != null ? user.getName() : "Desconhecido");
        log.setAccessLevel(requiredLevel);
        log.setStatus(result.getStatus());
        log.setDetails(result.getMessage());
        log.setConfidenceScore(result.getConfidence());
        
        logRepository.save(log);
    }

    private String getUserIdByLabel(int label) {
        return userLabelMap.entrySet().stream()
                .filter(entry -> entry.getValue() == label)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AccessLogRepository getLogRepository() {
        return logRepository;
    }

    public boolean isModelTrained() {
        return modelTrained;
    }
}

