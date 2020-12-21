package preprocessing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utilities.RandomGen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class AmazonDatasetPreprocess {

    public void retrieveFile() {

        JSONParser parser = new JSONParser();

        Object rawFile;
        try {
            rawFile = parser.parse(new FileReader("row-data-Amazon.json"));


            File productsFile = new File("products.json");
            File reviewsFile = new File("reviews.json");
            File usersFile = new File("users.json");

            final FileWriter fwP = new FileWriter(productsFile);
            final FileWriter fwR = new FileWriter(reviewsFile);
            final FileWriter fwU = new FileWriter(usersFile);

            JSONArray rawData = (JSONArray) rawFile;
            rawData.forEach(rawObj ->
            {
                parseProductObject((JSONObject) rawObj, fwP);
                parseReviewObject((JSONObject) rawObj, fwR);
                parseUserObject((JSONObject) rawObj, fwU);
            });

            fwP.close();
            fwR.close();
            fwU.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void parseProductObject(JSONObject rawProd, FileWriter fwP) {

        //Retrieve the details of the product
        String id = (String) rawProd.get("id");
        String name = (String) rawProd.get("name");
        String brand = (String) rawProd.get("brand");
        String[] categories = ((String) rawProd.get("categories")).split(",");
        String mainCategory = (String) rawProd.get("primaryCategories");

        //create a cleared product
        JSONObject product = new JSONObject();
        product.put("id", id);
        product.put("name", name);
        product.put("brand", brand);

        JSONArray array = new JSONArray();
        for (int i = 0; i < categories.length; i++) {
            array.add(categories[i]);
        }

        product.put("categories", array);
        product.put("mainCategory", mainCategory);

        try {
            fwP.append(product.toJSONString() + "," + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void parseReviewObject(JSONObject rawRev, FileWriter fwR) {

        //Retrieve the details of the review
        String date = (String) rawRev.get("reviews.date");
        Boolean doRecommend = (Boolean) rawRev.get("reviews.doRecommend");
        Long rate = (Long) rawRev.get("reviews.rating");
        String text = (String) rawRev.get("reviews.text");
        String title = (String) rawRev.get("reviews.title");
        String user = (String) rawRev.get("reviews.username");
        String product = (String) rawRev.get("id");

        //create a cleared product
        JSONObject review = new JSONObject();
        review.put("date", date);
        review.put("title", title);
        review.put("text", text);
        review.put("rate", rate);
        review.put("doRecommend", doRecommend);
        review.put("user", user);
        review.put("product", product);

        try {
            fwR.append(review.toJSONString() + "," + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseUserObject(JSONObject rawObj, FileWriter fwU) {

        //Retrieve the details of the review
        String username = (String) rawObj.get("reviews.username");
        String password = RandomGen.generateRandomPassword(8);

        //create a cleared product
        JSONObject user = new JSONObject();

        user.put("username", username);
        user.put("password", password);

        try {
            fwU.append(user.toJSONString() + "," + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}