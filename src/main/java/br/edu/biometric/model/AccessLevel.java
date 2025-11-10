package br.edu.biometric.model;

/**
 * Enum que define os níveis de acesso do sistema
 * Baseado nas regras de negócio do Ministério do Meio Ambiente
 */
public enum AccessLevel {

    NIVEL_1(1, "Nível 1 - Acesso Público", 
            "Informações básicas - todos podem acessar"),
    
    NIVEL_2(2, "Nível 2 - Diretores de Divisões", 
            "Informações restritas aos diretores de divisões"),
    
    NIVEL_3(3, "Nível 3 - Ministro do Meio Ambiente", 
            "Informações estratégicas - acesso exclusivo do ministro");

    private final int level;
    private final String displayName;
    private final String description;

    AccessLevel(int level, String displayName, String description) {
        this.level = level;
        this.displayName = displayName;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica se este nível de acesso pode acessar o recurso do nível especificado
     * @param requiredLevel Nível de acesso requerido
     * @return true se pode acessar, false caso contrário
     */
    public boolean canAccess(AccessLevel requiredLevel) {
        return this.level >= requiredLevel.level;
    }

    /**
     * Retorna o AccessLevel baseado no número do nível
     * @param level Número do nível (1, 2 ou 3)
     * @return AccessLevel correspondente
     */
    public static AccessLevel fromLevel(int level) {
        for (AccessLevel al : values()) {
            if (al.level == level) {
                return al;
            }
        }
        throw new IllegalArgumentException("Nível de acesso inválido: " + level);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

