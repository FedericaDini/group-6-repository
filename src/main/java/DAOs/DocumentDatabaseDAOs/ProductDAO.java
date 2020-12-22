package DAOs.DocumentDatabaseDAOs;

import beans.Product;
import beans.User;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import utilities.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ProductDAO {

    //Enter the database and find all the products that match the search String given by the user
    public static HashMap<String, String> findProductsByString(MongoDatabase database, String string) {

        HashMap<String, String> map = new HashMap<>();

        MongoCollection<Document> productsColl = database.getCollection("products");

        Document regexQuery = new Document();
        regexQuery.append("$regex", ".*" + Pattern.quote(string) + ".*");
        BasicDBObject criteria = new BasicDBObject("name", regexQuery);
        MongoCursor<Document> cursor = productsColl.find(criteria).iterator();

        while (cursor.hasNext()) {
            Document d = cursor.next();
            map.put(d.getString("id"), d.getString("name"));
        }

        return map;
    }

    //Enter the database and find a specific product
    public static Product findProductById(MongoDatabase database, String id) {

        Product p = null;

        MongoCollection<Document> productsColl = database.getCollection("products");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);
        MongoCursor<Document> cursor = productsColl.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            Document d = cursor.next();

            p = new Product(d.getString("id"), d.getString("name"), d.getString("brand"), d.getString("mainCategory"), d.get("categories"), availableItems ? ??,
            d.getDouble("price"), d.getString("description"), reviewsList ???,d.getDouble("rate"));
        }

        return u;


        Product p = new Product("ab12", "stdProd", "stdbrand", "stdCat", null, 20, 12.5, "std description", null, 4.0);
        return p;
    }

    public static HashMap<String, String> findSuggestedProductsByUsername(String username) {
        //MOCK
        HashMap<String, String> map = new HashMap<>();
        map.put("oo1", "p1");
        map.put("oo2", "p2");
        map.put("oo3", "p3");
        map.put("oo4", "p4");
        map.put("oo5", "p5");
        map.put("oo6", "p6");
        return map;
    }

    public static void insertProduct(Product p) {
        //Method to enter to the database and insert the product (MongoDB, Neo4J!!!)
        System.out.println("DONE.");
    }

    public static void insertProductToCart(Product p, User u) {
        //Method to enter to the KV database and insert a new product to the cart
        System.out.println("DONE.");
    }

    public static void updateProductQuantity(Product p, int q) {
        //Method to enter to the database and set a new value for the quantity of the product
        System.out.println("DONE.");
    }

    public static void deleteProduct(Product p) {
        //Method to enter to the database and delete the product (MongoDB, Neo4J, KV!!!)
        System.out.println("DONE.");
    }

}
