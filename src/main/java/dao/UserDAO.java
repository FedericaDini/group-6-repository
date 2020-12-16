package dao;

import beans.User;

import java.util.ArrayList;

public class UserDAO {

    public static User findUserByUsername(String username) {
        //Method to enter the database and search for a user given its credential
        return new User("pippo", "password", "CUST");
    }

    public static void addUser(User user) {
        //Method to enter the database and add a new user
    }

    public static boolean validUser(String username) {
        User user = UserDAO.findUserByUsername(username);
        if (user == null) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<String> findAllUsers() {
        //Method to enter the database and find all the usernames
        //MOCK
        ArrayList<String> usernames = new ArrayList<>();
        usernames.add("pippo");
        usernames.add("pluto");
        usernames.add("paperino");
        return usernames;
    }

    public static void deleteUser(User u) {
        //Method to delete the user from the database (MongoDB, Neo4J and KV!!!)
    }
}
