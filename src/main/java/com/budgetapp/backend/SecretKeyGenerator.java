package com.budgetapp.backend;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // Generate a cryptographically secure random 256-bit (32-byte) key
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(keyBytes);

        // Base64 encode the key bytes to get a string representation
        String base64EncodedKey = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("Generated JWT Secret Key (Base64 encoded):");
        System.out.println(base64EncodedKey);
    }
}