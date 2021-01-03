package DAOs.DocumentDatabaseDAOs;

import beans.Order;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import utilities.Types;
import utilities.Types.OrderState;

import java.text.SimpleDateFormat;
import java.util.*;

public class OrderDAO {

    //Method to enter to the database and find all the orders of the user (Key = id, Value = date)
    public static HashMap<String, String> findOrdersByUsername(MongoDatabase database, String username) {

        HashMap<String, String> map = new HashMap<>();

        MongoCollection<Document> ordersColl = database.getCollection("orders");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("user", username);
        MongoCursor<Document> cursor = ordersColl.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            Document d = cursor.next();

            String pattern = "dd/MM/yyyy HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            map.put(d.getObjectId("_id").toString(), "Date: " + simpleDateFormat.format(d.getDate("date")) + "\nTotal amount: " + Math.round(d.getDouble("totalAmount") * 100.0) / 100.0 + "\nState: " + d.getString("state") + "\n");
        }

        return map;
    }

    //Method to enter to the database and retrieve a specific order
    public static Order findOrderById(MongoDatabase database, String id) {
        Order o = null;

        MongoCollection<Document> ordersColl = database.getCollection("orders");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", new ObjectId(id));
        MongoCursor<Document> cursor = ordersColl.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            Document d = cursor.next();

            HashMap<String, Integer> productsIDs = new HashMap<>();

            List<Document> products = (List<Document>) d.get("products");
            if (products != null) {
                ListIterator<Document> iterator = products.listIterator();
                while (iterator.hasNext()) {
                    Document product = iterator.next();
                    String prodID = product.getString("product");
                    Integer quantity = product.getInteger("quantity");
                    productsIDs.put(prodID, quantity);
                }
            }

            String s = d.getString("state");
            Types.OrderState state = null;
            if (s.equals(OrderState.OPENED.toString())) {
                state = OrderState.OPENED;
            } else if (s.equals(OrderState.IN_TRANSIT.toString())) {
                state = OrderState.IN_TRANSIT;
            } else if (s.equals(OrderState.CLOSED.toString())) {
                state = OrderState.CLOSED;
            }

            o = new Order(id, productsIDs, d.getDouble("totalAmount"), d.getString("user"), state);
        }

        return o;
    }

    //Method to enter to the database and add a specific order
    public static void insertOrder(MongoDatabase database, Order order) {

        MongoCollection<Document> ordersColl = database.getCollection("orders");

        ArrayList<Document> products = new ArrayList<>();

        HashMap<String, Integer> prods = order.getProducts();
        for (String key : prods.keySet()) {
            String prodID = key;
            Integer quantity = prods.get(key);

            Document d = new Document("product", prodID)
                    .append("quantity", quantity);
            products.add(d);
        }

        Document o = new Document("user", order.getUserId())
                .append("products", products)
                .append("totalAmount", order.getTotalAmount())
                .append("date", order.getDate())
                .append("state", OrderState.OPENED.toString());

        ordersColl.insertOne(o);
        System.out.println("DONE." + "\n");
    }

    //Method to enter to the database and delete a specific order
    public static void deleteOrder(MongoDatabase database, String id) {
        MongoCollection<Document> ordersColl = database.getCollection("orders");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", new ObjectId(id));
        ordersColl.deleteOne(whereQuery);
        System.out.println("DONE." + "\n");
    }

    //Method to enter to the database and set a new value for the quantity of the product
    public static void updateOrderState(MongoDatabase database, String id, String s) {

        MongoCollection<Document> ordersColl = database.getCollection("orders");

        BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(id));
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set", new BasicDBObject().append("state", s));
        ordersColl.updateOne(searchQuery, updateQuery);
        System.out.println("DONE." + "\n");
    }
}
