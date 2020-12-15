package dao;

import beans.Product;

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
}
