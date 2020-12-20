import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataChanges {

    private JSONArray rawProducts;

    public void parseFile() {

        File productsFile = new File("products.json");

        if (productsFile.exists()) {
            productsFile.delete();
        }

        try {

            final FileWriter fw = new FileWriter(productsFile);

            JSONParser parser = new JSONParser();

            Object rawData = parser.parse(new FileReader("row-data-Amazon.json"));
            rawProducts = (JSONArray) rawData;

            rawProducts.forEach(rawProd -> parseProductObject((JSONObject) rawProd, fw));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseProductObject(JSONObject rawProd, FileWriter fw){

        //retrive the details of the product
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
            fw.append(product.toJSONString() + "," + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
        //reviews
        String reviewDate = (String) employee.get("reviews.date");
        Boolean reviewDoRaccomend = (Boolean) employee.get("reviews.doRecommend");
        Long reviewRating = (Long) employee.get("reviews.rating");
        String reviewText = (String) employee.get("reviews.text");
        String reviewTitle = (String) employee.get("reviews.title");
        String reviewUsername = (String) employee.get("reviews.Username");


        newReview.put("id", reviews_id);
        newReview.put("date", reviewDate);
        newReview.put("doRaccomend", reviewDoRaccomend);
        newReview.put("rating", reviewRating);
        newReview.put("text", reviewText);
        newReview.put("title", reviewTitle);
        newReview.put("username", reviewUsername);


    }*/
}