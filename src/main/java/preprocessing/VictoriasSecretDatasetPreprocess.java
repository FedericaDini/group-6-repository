package preprocessing;

import DAOs.DocumentDatabaseDAOs.ConnectionToMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.RandomGen;

import java.io.FileReader;
import java.util.Arrays;

public class VictoriasSecretDatasetPreprocess {


    private static ConnectionToMongoDB connection = null;
    private static MongoDatabase database = null;

    @SuppressWarnings("unchecked")
    public void retrieveProductsFile(String path) {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader(path));

            JSONArray rawData = (JSONArray) rawFile;
            rawData.forEach(rawObj -> parseProductObject((JSONObject) rawObj));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private static void parseProductObject(JSONObject rawProd) {

        //Retrieve the ID of the product
        String id = RandomGen.generateRandomString(10);

        //Check if the product is already present in the database
        MongoCollection<Document> productsColl = database.getCollection("products");
        boolean insert = connection.isNewID(productsColl, id);

        if (insert) {

            //Retrieve the details of the product
            String name = (String) rawProd.get("product_name");
            String brand = (String) rawProd.get("brand_name");
            String mainCategory = (String) rawProd.get("product_category");
            String price = (String) rawProd.get("price");
            String description = (String) rawProd.get("description");
            String color = (String) rawProd.get("color");
            if (color != null) {
                name = name.concat(" - " + color);
            }

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
    public void retrieveReviewsFile(String path) {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader(path));

            JSONArray rawData = (JSONArray) rawFile;
            rawData.forEach(rawObj -> parseReviewObject((JSONObject) rawObj));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void parseReviewObject(JSONObject rawRev) {
        MongoCollection<Document> reviewsColl = database.getCollection("reviews");

        //Retrieve the details of the review
        Long recommended_ind = (Long) rawRev.get("Recommended IND");
        Long rating = (Long) rawRev.get("Rating");
        double rate = rating.doubleValue();
        String text = (String) rawRev.get("Review Text");
        String title = (String) rawRev.get("Title");

        boolean doRecommend = false;
        if (recommended_ind == 1) {
            doRecommend = true;
        }

        //create a cleared review
        Document review = new Document("date", RandomGen.generateRandomDate())
                .append("doRecommend", doRecommend)
                .append("rate", rate)
                .append("text", text)
                .append("title", title)
                .append("user", returnRandomUsername())
                .append("product", returnRandomProductIdOfVS());

        //Insert the new object into MongoDB
        reviewsColl.insertOne(review);
    }

    public static String returnRandomUsername() {
        MongoCollection<Document> usersColl = database.getCollection("users");
        AggregateIterable<Document> iterable = usersColl.aggregate(Arrays.asList(Aggregates.sample(1)));
        Document d = iterable.first();
        String username = d.getString("username");
        return username;
    }

    public static String returnRandomProductIdOfVS() {
        MongoCollection<Document> productsColl = database.getCollection("products");
        BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("brand", new BasicDBObject("$eq", "Victoria's Secret")));
        BasicDBObject sample = new BasicDBObject("$sample", new BasicDBObject("size", 1));
        AggregateIterable<Document> iterable = productsColl.aggregate(Arrays.asList(match, sample));
        Document d = iterable.first();
        String prodId = d.getString("_id");
        return prodId;
    }


    public static void main(String[] args) {

        connection = new ConnectionToMongoDB();
        connection.openConnection("mongodb://localhost:27017");

        database = connection.getMongoDatabase();

        VictoriasSecretDatasetPreprocess victoriasSecretDatasetPreprocess = new VictoriasSecretDatasetPreprocess();
        /*victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-Victoriassecret.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-CalvinKlein.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-HankyPanky.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-Amazon2.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-Nordstrom.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-Ae.json");
        victoriasSecretDatasetPreprocess.retrieveProductsFile("raw-data-Btemptd.json");*/

        victoriasSecretDatasetPreprocess.retrieveReviewsFile("raw-data-Reviews-VS.json");

        victoriasSecretDatasetPreprocess.connection.closeConnection();
    }


}
