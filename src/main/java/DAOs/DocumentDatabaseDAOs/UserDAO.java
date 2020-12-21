package DAOs.DocumentDatabaseDAOs;

import beans.User;
import utilities.Types.UserType;

import java.util.ArrayList;

public class UserDAO {

    public static ArrayList<String> findAllUsers() {
        //Method to enter the database and find all the usernames
        //MOCK
        ArrayList<String> usernames = new ArrayList<>();
        usernames.add("pippo");
        usernames.add("pluto");
        usernames.add("paperino");
        return usernames;
    }

    public static User findUserByUsername(String username) {
        //Method to enter the database and search for a user given its credential
        if (username.equals("pippo")) {
            return new User("pippo", "password", UserType.CUST);
            //return new User("pippo", "password", UserType.ADMIN);
            //return new User("pippo", "password", UserType.EMP);
        } else {
            return null;
        }
    }

    public static boolean validUser(String username) {
        //User user = UserDAO.findUserByUsername(username);

        //MOCK
        User user = null;

        if (user == null) {
            return true;
        } else {
            return false;
        }
    }

    public static void addUser(User user) {
        //Method to enter the database and add a new user
        System.out.println("DONE.");
    }

    public static void deleteUser(User u) {
        //Method to delete the user from the database (MongoDB, Neo4J and KV!!!)
        //we have also to delete the reviews, the cart anc the orders



        System.out.println("DONE.");
    }
}
