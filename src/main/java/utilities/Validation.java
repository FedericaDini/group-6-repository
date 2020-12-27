package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Validation {
    public static boolean validatePassword(String password, int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (password.length() < length) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            if (!characters.contains(String.valueOf(password.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static int takePositiveInt(BufferedReader inKeyboard, PrintWriter outVideo) {
        int n = -1;
        while (n == -1) {
            try {
                String number = inKeyboard.readLine();
                n = Integer.parseInt(number);

                if (n <= 0) {
                    n = -1;
                    outVideo.println("Insert a non-negative number");
                }
            } catch (NumberFormatException nfe) {
                outVideo.println("You must insert a number");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return n;
    }

    public static double takeValidPrice(BufferedReader inKeyboard, PrintWriter outVideo) {

        double price = 0.0;
        while (price == 0.0) {
            try {
                price = Double.parseDouble(inKeyboard.readLine());
                if (price < 0) {
                    price = 0.0;
                    outVideo.println("Products cannot be free!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException nfe) {
                outVideo.println("You must insert a price number");
            }
        }

        price = Math.round(price * 100.0) / 100.0;

        return price;
    }

    public static String takeMandatoryString(BufferedReader inKeyboard, PrintWriter outVideo) {
        String field = null;
        while (field == null) {
            try {
                field = inKeyboard.readLine();

                if (field.equals("")) {
                    field = null;
                    outVideo.println("This field is mandatory, retry!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return field;
    }
}
