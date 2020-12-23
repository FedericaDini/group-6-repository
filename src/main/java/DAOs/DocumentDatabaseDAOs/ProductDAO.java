package DAOs.DocumentDatabaseDAOs;

import beans.Product;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ProductDAO {

    //Enter the database and find all the products that match the search String given by the user
    public static HashMap<String, String> findProductsByString(MongoDatabase database, String string) {

        HashMap<String, String> map = new HashMap<>();

        MongoCollection<Document> productsColl = database.getCollection("products");

        Pattern pattern = Pattern.compile(".*" + Pattern.quote(string) + ".*", Pattern.CASE_INSENSITIVE);
        MongoCursor<Document> cursor = productsColl.find(Filters.regex("name", pattern)).iterator();

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

            ArrayList<String> categories = new ArrayList<>();

            MongoCollection<Document> categoriesCollection = (MongoCollection<Document>) d.get("categories");
            MongoCursor<Document> cursor2 = categoriesCollection.find().iterator();
            while (cursor2.hasNext()) {
                Document cat = cursor2.next();
                String category = cat.getString("category");
                categories.add(category);
            }

            p = new Product(d.getString("id"), d.getString("name"), d.getString("brand"), d.getString("mainCategory"), categories, d.getDouble("price"), d.getString("description"), ReviewDAO.findReviewsByProductId(database, id), d.getDouble("rate"));
        }

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

    public static void insertProduct(MongoDatabase database, Product product) {

        MongoCollection<Document> productsColl = database.getCollection("products");

        Document p = new Document("name", product.getName())
                .append("brand", product.getBrand())
                .append("mainCategory", product.getMainCategory())
                .append("categories", product.getCategories())
                .append("description", product.getDescription())
                .append("price", product.getPrice());

        productsColl.insertOne(p);
        System.out.println("DONE.");
    }


    public static void deleteProduct(MongoDatabase database, String id) {

        MongoCollection<Document> productsColl = database.getCollection("products");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
        productsColl.deleteOne(whereQuery);

        System.out.println("DONE.");
    }

}
