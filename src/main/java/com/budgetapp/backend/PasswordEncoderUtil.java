package com.budgetapp.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {

        String plainTextPassword = "password123";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(plainTextPassword);

        System.out.println("Plain Text Password: " + plainTextPassword);
        System.out.println("Encoded Password: " + encodedPassword);
    }
}