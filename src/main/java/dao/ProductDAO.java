package dao;

import beans.Product;
import beans.User;

import java.util.ArrayList;
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

    public static void insertProductToCart(Product p, User u) {
        //Method to enter to the KV database and insert a new product to the cart
    }

    public static void updateProductQuantity(Product p, int q) {
        //Method to enter to the database and set a new value for the quantity of the product
    }

    public static void insertProduct(Product p) {
        //Method to enter to the database and insert the product (MongoDB, Neo4J!!!)
    }

    public static void deleteProduct(Product p) {
        //Method to enter to the database and delete the product (MongoDB, Neo4J, KV!!!)
    }

    public static HashMap<String, String> findCartProductsByUser(User u) {
        //Method to enter the KV database and retrieve the list of the ids and names of the user's cart products

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

    public static void removeFromCart(String id, User user) {
        //Method to enter the KV database and remove the product from the cart
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
}
