package DAOs.DocumentDatabaseDAOs;

import beans.Product;
import beans.User;
import com.mongodb.client.MongoDatabase;

import java.util.HashMap;

public class ProductDAO {

    public static HashMap<String, String> findProductsByString(String string) {
        //Method to enter to the database and find all the products with a specific string in the name
        HashMap<String, String> map = new HashMap<>();
        map.put("oo1", "p1");
        map.put("oo2", "p2");
        map.put("oo3", "p3");
        map.put("oo4", "p4");
        map.put("oo5", "p5");
        map.put("oo6", "p6");

        return map;
    }

    public static Product findProductById(String id) {
        //Method to enter to the database and find the product with a specific ID
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
