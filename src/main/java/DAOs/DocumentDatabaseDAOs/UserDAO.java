package DAOs.DocumentDatabaseDAOs;

import beans.User;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import utilities.Types.UserType;

import java.util.ArrayList;

public class UserDAO {

    //Enter the database and find all the usernames
    public static ArrayList<String> findAllUsernames(MongoDatabase database) {

        ArrayList<String> usersList = new ArrayList<>();

        MongoCollection<Document> usersColl = database.getCollection("users");

        MongoCursor<Document> cursor = usersColl.find().iterator();
        while (cursor.hasNext()) {
            Document d = cursor.next();
            usersList.add(d.getString("username"));
        }

        return usersList;
    }

    //Enter the database and find the user with a specific username
    public static User findUserByUsername(MongoDatabase database, String username) {

        MongoCollection<Document> usersColl = database.getCollection("users");
        User u = null;

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("username", username);
        MongoCursor<Document> cursor = usersColl.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            Document d = cursor.next();

            String t = d.getString("type");
            UserType type = null;
            if (t.equals(UserType.ADMIN.toString())) {
                type = UserType.ADMIN;
            } else if (t.equals(UserType.CUSTOMER.toString())) {
                type = UserType.CUSTOMER;
            } else if (t.equals(UserType.EMPLOYEE.toString())) {
                type = UserType.EMPLOYEE;
            }

            u = new User(username, d.getString("password"), type);
        }

        return u;
    }

    //Enter the database and check if the given username is new
    public static boolean validUser(MongoDatabase database, String username) {
        User user = findUserByUsername(database, username);
        return user == null;
    }

    //Enter the database and add a new user
    public static void addUser(MongoDatabase database, User user) {

        MongoCollection<Document> usersColl = database.getCollection("users");

        Document u = new Document("username", user.getUsername())
                .append("password", user.getPassword())
                .append("type", user.getType().toString());

        usersColl.insertOne(u);
        System.out.println("DONE." + "\n");
    }

    //Delete the user from the database
    public static void deleteUser(MongoDatabase database, String username) {
        MongoCollection<Document> usersColl = database.getCollection("users");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("username", username);
        usersColl.deleteOne(whereQuery);
        System.out.println("DONE." + "\n");
    }
}
