package br.edu.biometric.model;

/**
 * Enum que define os possíveis status de uma tentativa de acesso
 */
public enum AccessStatus {

    SUCCESS("Acesso Concedido", "success"),
    DENIED_NOT_RECOGNIZED("Acesso Negado - Não Reconhecido", "danger"),
    DENIED_LOW_CONFIDENCE("Acesso Negado - Baixa Confiança", "warning"),
    DENIED_INACTIVE_USER("Acesso Negado - Usuário Inativo", "danger"),
    DENIED_INSUFFICIENT_PERMISSION("Acesso Negado - Permissão Insuficiente", "danger"),
    ERROR("Erro no Sistema", "danger");

    private final String displayName;
    private final String severity; // success, warning, danger

    AccessStatus(String displayName, String severity) {
        this.displayName = displayName;
        this.severity = severity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

