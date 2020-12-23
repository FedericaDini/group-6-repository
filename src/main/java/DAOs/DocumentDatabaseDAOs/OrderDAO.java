package DAOs.DocumentDatabaseDAOs;

import beans.Order;
import beans.Product;
import utilities.Types.OrderState;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OrderDAO {

    public static HashMap<String, String> findOrdersByUserId(String id) {
        //Method to enter to the database and find all the orders of the user (Key = id, Value = date)
        HashMap<String, String> map = new HashMap<>();
        map.put("oo1", new Date().toString());
        map.put("oo2", new Date().toString());
        map.put("oo3", new Date().toString());
        map.put("oo4", new Date().toString());
        map.put("oo5", new Date().toString());
        map.put("oo6", new Date().toString());

        return map;
    }

    public static Order findOrderById(String id) {
        //Method to enter the database and find the order given the id
        //MOCK
        Product p1 = new Product("ab12", "stdProd", "stdbrand", "stdCat", null, 12.5, "std description", null, 4.0);
        Product p2 = new Product("cd45", "stdProd", "stdbrand", "stdCat", null, 12.5, "std description", null, 4.0);
        Product p3 = new Product("ef67", "stdProd", "stdbrand", "stdCat", null, 12.5, "std description", null, 4.0);

        ArrayList<Product> productsList = new ArrayList<>();
        productsList.add(p1);
        productsList.add(p2);
        productsList.add(p3);

        Order o = new Order(productsList, 37.5, "pippo", OrderState.OPENED);

        return o;
    }

    public static void insertOrder(Order order) {
        //Method to insert a new order (in MongoDB and Neo4J!!!)
        //We have to set the date (real date of the order)
        System.out.println("DONE.");
    }

    public static void deleteOrder(Order o) {
        //Method to enter to the database and delete the product (MongoDB, Neo4J!!!)
        System.out.println("DONE.");
    }

    public static void updateOrderState(Order o, OrderState s) {
        //Method to enter to the database and set a new value for the quantity of the product
        System.out.println("DONE.");
    }


}
