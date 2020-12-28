package preprocessing;

import DAOs.DocumentDatabaseDAOs.ConnectionToMongoDB;
import DAOs.DocumentDatabaseDAOs.ReviewDAO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.RandomGen;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VictoriasSecretDatasetPreprocess {


    private static ConnectionToMongoDB connection = null;
    private static MongoDatabase database = null;

    @SuppressWarnings("unchecked")
    public void retrieveFile() {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader("row-data-Victoriassecret.json"));

            JSONObject results = (JSONObject) rawFile;


            JSONArray rawData = (JSONArray) results.get("results");
            rawData.forEach(rawObj -> parseProductObject((JSONObject) rawObj));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private static void parseProductObject(JSONObject rawProd) {

        //Retrieve the ID of the product
        String id = (String) RandomGen.generateRandomString(10);

        //Check if the product is already present in the database
        MongoCollection<Document> productsColl = database.getCollection("products");
        boolean insert = connection.isNewElement(productsColl, id);

        if (insert) {

            //Retrieve the details of the product
            String name = (String) rawProd.get("product_name");
            String brand = (String) rawProd.get("brand_name");
            String mainCategory = (String) rawProd.get("product_category");
            String price = (String) rawProd.get("price");
            price = price.substring(1);
            String description = (String) rawProd.get("description");



            //create a cleared product
            Document product = new Document("_id", id)
                    .append("name", name)
                    .append("brand", brand)
                    .append("mainCategory", mainCategory)
                    .append("price", Double.parseDouble(price))
                    .append("description", description)
                    .append("rate", 0.0);

            //Insert the new object into MongoDB
            productsColl.insertOne(product);
        }
    }

    @SuppressWarnings("unchecked")
    public void retrieveFile2() {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader("row-data-Reviews-VS.json"));

            JSONObject results = (JSONObject) rawFile;


            JSONArray rawData = (JSONArray) results.get("results");
            rawData.forEach(rawObj -> parseReviewObject((JSONObject) rawObj));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void parseReviewObject(JSONObject rawRev) {

        MongoCollection<Document> reviewsColl = database.getCollection("reviews");

        //Retrieve the details of the review
        String dateS = (String) rawRev.get("reviews.date");

        Date date = null;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Boolean doRecommend = (Boolean) rawRev.get("reviews.doRecommend");
        Long rating = (Long) rawRev.get("reviews.rating");
        double rate = rating.doubleValue();
        String text = (String) rawRev.get("reviews.text");
        String title = (String) rawRev.get("reviews.title");
        String user = (String) rawRev.get("reviews.username");
        String product = (String) rawRev.get("id");

        //create a cleared review
        Document review = new Document("date", date)
                .append("doRecommend", doRecommend)
                .append("rate", rate)
                .append("text", text)
                .append("title", title)
                .append("user", user)
                .append("product", product);

        //Insert the new object into MongoDB
        reviewsColl.insertOne(review);
    }


    public static void main(String[] args) {

        connection = new ConnectionToMongoDB();
        connection.openConnection("mongodb://localhost:27017");

        database = connection.getMongoDatabase();

        VictoriasSecretDatasetPreprocess victoriasSecretDatasetPreprocess = new VictoriasSecretDatasetPreprocess();
        victoriasSecretDatasetPreprocess.retrieveFile();

        connection.closeConnection();
    }


}
