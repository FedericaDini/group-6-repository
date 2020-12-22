package utilities;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static double generateRandomPrice() {

        double min = 90;
        double max = 650;
        double random = new Random().nextDouble();
        double price = min + (random * (max - min));

        price = Math.round(price * 100.0) / 100.0;

        return price;
    }
}
