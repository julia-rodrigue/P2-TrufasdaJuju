package com.example.sitetrufas.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitário simples de hash de senha usando SHA-256.
 *
 * Observação: este projeto não utiliza Spring Security por escolha de
 * simplicidade. O hash SHA-256 evita guardar a senha em texto puro,
 * mas não é tão robusto quanto BCrypt (não usa salt nem custo
 * configurável). Para um ambiente de produção real, o ideal seria
 * migrar para BCrypt via Spring Security.
 */
public class HashUtil {

    private HashUtil() {}

    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 não disponível", e);
        }
    }
}
