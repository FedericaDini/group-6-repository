package it.unipi.lsdb.DAOs.DocumentDatabaseDAOs;

import it.unipi.lsdb.beans.Product;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class ProductDAO {

    //Enter the database and find all the products that match the search String given by the user
    public static HashMap<String, String> findProductsByString(MongoDatabase database, String string) {

        HashMap<String, String> map = new HashMap<>();

        MongoCollection<Document> productsColl = database.getCollection("products");

        //Query using an index provided by MongoDB
        FindIterable<Document> cursor = productsColl.find(new BasicDBObject("$text", new BasicDBObject("$search", string)));

        for (Document d : cursor) {
            map.put(d.getString("_id"), d.getString("name"));
        }

        return map;
    }

    //Enter the database and find a specific product
    public static Product findProductById(MongoDatabase database, String id) {

        Product p = null;

        MongoCollection<Document> productsColl = database.getCollection("products");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
        MongoCursor<Document> cursor = productsColl.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            Document d = cursor.next();

            ArrayList<String> categoriesList = new ArrayList<>();

            List<String> categories = (List<String>) d.get("categories");
            if (categories != null) {
                ListIterator<String> iterator = categories.listIterator();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    categoriesList.add(s);
                }
            }

            p = new Product(d.getString("_id"), d.getString("name"), d.getString("brand"), d.getString("mainCategory"), categoriesList, d.getDouble("price"), d.getString("description"), ReviewDAO.findReviewsByProductId(database, id), d.getDouble("rate"));
        }

        return p;
    }

    public static void insertProduct(MongoDatabase database, Product product) {

        MongoCollection<Document> productsColl = database.getCollection("products");

        Document p = new Document("_id", product.getId())
                .append("name", product.getName())
                .append("brand", product.getBrand())
                .append("mainCategory", product.getMainCategory())
                .append("categories", product.getCategories())
                .append("description", product.getDescription())
                .append("price", product.getPrice())
                .append("rate", product.getRate());

        productsColl.insertOne(p);
        System.out.println("DONE." + "\n");
    }


    public static void deleteProduct(MongoDatabase database, String id) {

        MongoCollection<Document> productsColl = database.getCollection("products");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
        productsColl.deleteOne(whereQuery);
        System.out.println("DONE." + "\n");
    }

}
