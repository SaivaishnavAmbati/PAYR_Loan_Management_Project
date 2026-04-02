package com.cartigo.authservice.util;

import java.security.SecureRandom;

public class OtpGenerator {

    private static final SecureRandom RAND = new SecureRandom();

    private OtpGenerator() {}

    public static String generate6DigitOtp() {
        int n = 100000 + RAND.nextInt(900000);
        return String.valueOf(n);
    }
}
