package br.edu.biometric.util;

import java.util.regex.Pattern;

/**
 * Utilitários para validação de dados
 */
public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Valida um email
     * 
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
     * Valida um CPF com dígitos verificadores (algoritmo módulo 11)
     * 
     * @param cpf CPF a ser validado
     * @return true se o CPF é válido
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
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Valida dígitos verificadores
        try {
            // Calcula primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cleanCpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }

            // Verifica primeiro dígito
            if (firstDigit != Character.getNumericValue(cleanCpf.charAt(9))) {
                return false;
            }

            // Calcula segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cleanCpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }

            // Verifica segundo dígito
            return secondDigit == Character.getNumericValue(cleanCpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formata um CPF
     * 
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
     * 
     * @param value String a ser validada
     * @return true se não está vazia
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida se um nome é válido (mínimo 3 caracteres, apenas letras e espaços)
     * 
     * @param name Nome a ser validado
     * @return true se o nome é válido
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().length() < 3) {
            return false;
        }
        // Permite letras (incluindo acentuadas) e espaços
        return name.trim().matches("^[a-zA-ZÀ-ÿ\\s]+$");
    }

    /**
     * Valida se uma string tem tamanho mínimo
     * 
     * @param value     String a ser validada
     * @param minLength Tamanho mínimo
     * @return true se tem o tamanho mínimo
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }
}
