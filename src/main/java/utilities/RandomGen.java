package utilities;

import java.security.SecureRandom;

public class RandomGen {
    public static String generateRandomPassword(int length) {
        // Possible characters inside the password (0-9, a-z, A-Z)
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        // Choose a character randomly and append it to the new password
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
}
