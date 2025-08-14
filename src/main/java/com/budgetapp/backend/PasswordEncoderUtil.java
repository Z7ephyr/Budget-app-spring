package com.budgetapp.backend; // Or whatever package you put it in

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {
        // This is the plain-text password you want to hash
        String plainTextPassword = "password123"; // You can change this to any plain-text password you like

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(plainTextPassword);

        System.out.println("Plain Text Password: " + plainTextPassword);
        System.out.println("Encoded Password: " + encodedPassword);
    }
}