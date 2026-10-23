package com.mycompany.college.saint.raphael.utils;
import java.time.LocalDate;
import java.util.regex.Pattern;
public final class ValidationUtils {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private ValidationUtils() {
    }
    public static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " est obligatoire.");
        return value.trim();
    }
    public static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
    public static String email(String value) {
        String v = optional(value);
        if (v != null && !EMAIL.matcher(v).matches()) throw new IllegalArgumentException("L'adresse e-mail est invalide.");
        return v;
    }
    public static LocalDate birthDate(LocalDate value) {
        if (value == null) throw new IllegalArgumentException("La date de naissance est obligatoire.");
        if (value.isAfter(LocalDate.now())) throw new IllegalArgumentException("La date de naissance ne peut pas être future.");
        return value;
    }
    public static int positive(int value, String field) {
        if (value <= 0) throw new IllegalArgumentException(field + " doit être supérieur à zéro.");
        return value;
    }
}
