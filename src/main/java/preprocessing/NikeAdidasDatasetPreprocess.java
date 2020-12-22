package preprocessing;

import DAOs.DocumentDatabaseDAOs.ConnectionToMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.RandomGen;

import java.io.FileReader;

@SuppressWarnings("unchecked")
public class NikeAdidasDatasetPreprocess {

    private static ConnectionToMongoDB connection = null;
    private static MongoDatabase database = null;

    public void retrieveFile(String path) {

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
        String id = rawProd.get("Product ID").toString();

        //Check if the product is already present in the database
        MongoCollection<Document> productsColl = database.getCollection("products");
        boolean insert = connection.isNewElement(productsColl, id);

        if (insert) {

            //Retrieve the details of the product
            String name = (String) rawProd.get("Product Name");
            String brand = (String) rawProd.get("Brand");
            String description = (String) rawProd.get("Description");


            //create a cleared product
            Document product = new Document("id", id)
                    .append("name", name)
                    .append("brand", brand)
                    .append("mainCategory", "Shoes & Clothing")
                    .append("description", description)
                    .append("price", RandomGen.generateRandomPrice(70, 250))
                    .append("rate", 0.0);

            //Insert the new object into MongoDB
            productsColl.insertOne(product);
        }
    }

    public static void main(String[] args) {

        connection = new ConnectionToMongoDB();
        connection.openConnection("mongodb://localhost:27017");

        database = connection.getMongoDatabase();

        NikeAdidasDatasetPreprocess nikeAdidasDatasetPreprocess = new NikeAdidasDatasetPreprocess();
        nikeAdidasDatasetPreprocess.retrieveFile("row-data-Nike.json");
        nikeAdidasDatasetPreprocess.retrieveFile("row-data-Adidas.json");


        connection.closeConnection();
    }

}
