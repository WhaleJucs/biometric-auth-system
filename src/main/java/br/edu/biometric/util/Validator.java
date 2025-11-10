package br.edu.biometric.util;

import java.util.regex.Pattern;

/**
 * Utilitários para validação de dados
 */
public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern CPF_PATTERN = Pattern.compile(
            "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$"
    );

    /**
     * Valida um email
     * @param email Email a ser validado
     * @return true se o email é válido
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida um CPF (formato básico)
     * @param cpf CPF a ser validado
     * @return true se o CPF tem formato válido
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        // Remove formatação
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        // Verifica se tem 11 dígitos
        if (cleanCpf.length() != 11) {
            return false;
        }
        // Verifica se não são todos os dígitos iguais
        return !cleanCpf.matches("(\\d)\\1{10}");
    }

    /**
     * Formata um CPF
     * @param cpf CPF sem formatação
     * @return CPF formatado (XXX.XXX.XXX-XX)
     */
    public static String formatCpf(String cpf) {
        if (cpf == null) {
            return "";
        }
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        if (cleanCpf.length() != 11) {
            return cpf;
        }
        return String.format("%s.%s.%s-%s",
                cleanCpf.substring(0, 3),
                cleanCpf.substring(3, 6),
                cleanCpf.substring(6, 9),
                cleanCpf.substring(9, 11));
    }

    /**
     * Valida se uma string não está vazia
     * @param value String a ser validada
     * @return true se não está vazia
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

