package dao;

import beans.User;

public class UserDAO {

    public static User findUserByCredentials(String username, String password) {
        //Method to enter the database and search for a user given its credential
        if (username.equals("pippo") && password.equals("password")) {
            return new User("pippo", "password", "CUST");
        } else {
            return null;
        }
    }

    public static void addUser(User user) {
        //Method to enter the database and add a new user
    }
}
