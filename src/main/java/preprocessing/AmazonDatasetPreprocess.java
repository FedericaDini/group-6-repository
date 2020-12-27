package preprocessing;

import DAOs.DocumentDatabaseDAOs.ConnectionToMongoDB;
import DAOs.DocumentDatabaseDAOs.ReviewDAO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.RandomGen;
import utilities.Types;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AmazonDatasetPreprocess {

    private static ConnectionToMongoDB connection = null;
    private static MongoDatabase database = null;

    @SuppressWarnings("unchecked")
    public void retrieveFile() {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader("row-data-Amazon.json"));

            JSONArray rawData = (JSONArray) rawFile;
            rawData.forEach(rawObj ->
            {
                parseReviewObject((JSONObject) rawObj);
                parseProductObject((JSONObject) rawObj);
                parseUserObject((JSONObject) rawObj);
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void parseProductObject(JSONObject rawProd) {

        //Retrieve the ID of the product
        String id = (String) rawProd.get("id");

        //Check if the product is already present in the database
        MongoCollection<Document> productsColl = database.getCollection("products");
        boolean insert = connection.isNewElement(productsColl, id);

        if (insert) {

            //Retrieve the details of the product
            String name = (String) rawProd.get("name");
            String brand = (String) rawProd.get("brand");
            String[] categories = ((String) rawProd.get("categories")).split(",");
            String mainCategory = (String) rawProd.get("primaryCategories");

            ArrayList<String> catsList = new ArrayList<>();
            for (int i = 0; i < categories.length; i++) {
                catsList.add(categories[i]);
            }

            //create a cleared product
            Document product = new Document("_id", id)
                    .append("name", name)
                    .append("brand", brand)
                    .append("categories", catsList)
                    .append("mainCategory", mainCategory)
                    .append("price", RandomGen.generateRandomPrice(90, 650))
                    .append("rate", ReviewDAO.computeAvgReviewsRateByProductId(database, id));

            //Insert the new object into MongoDB
            productsColl.insertOne(product);
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

    private void parseUserObject(JSONObject rawObj) {

        //Retrieve the user of the product
        String username = (String) rawObj.get("reviews.username");

        //Check if the user is already present in the database
        MongoCollection<Document> usersColl = database.getCollection("users");
        boolean insert = connection.isNewElement(usersColl, username);

        if (insert) {

            //create a cleared user
            Document user = new Document("username", username)
                    .append("password", RandomGen.generateRandomString(8))
                    .append("type", Types.UserType.CUSTOMER.toString());

            //Insert the new object into MongoDB
            usersColl.insertOne(user);
        }
    }

    public static void main(String[] args) {

        connection = new ConnectionToMongoDB();
        connection.openConnection("mongodb://localhost:27017");

        database = connection.getMongoDatabase();

        AmazonDatasetPreprocess amazonDatasetPreprocess = new AmazonDatasetPreprocess();
        amazonDatasetPreprocess.retrieveFile();

        connection.closeConnection();
    }
}