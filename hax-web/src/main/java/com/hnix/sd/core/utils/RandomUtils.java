package com.hnix.sd.core.utils;

import java.security.SecureRandom;

public class RandomUtils {

    private static final String CHARACTERS  = "ekfisWBVAilqi223kdivsazpz01qmnwqASDPIO";

    public static int randomNumber(int length) {
        SecureRandom random = new SecureRandom();
        int upperLimit = (int) Math.pow(10, length);
        return random.nextInt(upperLimit);
    }

    public static String randomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
