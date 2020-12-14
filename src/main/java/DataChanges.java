import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class DataChanges {
    public void go(){
    JSONParser parser = new JSONParser();
        try {
        Object obj = parser.parse(new FileReader("C:/Users/Giovanni/Desktop/aaaaa.json"));
        JSONArray employeeList = (JSONArray) obj;
        JSONObject newObj=new JSONObject();
        employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp, newObj, newObj) );
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private static void parseEmployeeObject(JSONObject employee,JSONObject newJson, JSONObject newReview)
    {
        //products

        String id = (String) employee.get("id");
        String name = (String) employee.get("name");
        String brand = (String) employee.get("brand");


        String[] categories = ((String) employee.get("categories")).split(",");
        JSONArray array = new JSONArray();
        for(int i = 0; i < categories.length; i++) {
            array.add(categories[i]);
        }

        String primaryCategories = (String) employee.get("primaryCategories");
        Long reviews_id = (Long) employee.get("reviews.id");



        newJson.put("id",id);
        newJson.put("name",name);
        newJson.put("brand",brand);
        newJson.put("categories",array);
        newJson.put("primaryCategories",primaryCategories);
        newJson.put("idReview",reviews_id);








        //reviews
        String reviewDate = (String) employee.get("reviews.date");
        Boolean reviewDoRaccomend = (Boolean) employee.get("reviews.doRecommend");
        Long reviewRating = (Long) employee.get("reviews.rating");
        String reviewText = (String) employee.get("reviews.text");
        String reviewTitle = (String) employee.get("reviews.title");
        String reviewUsername = (String) employee.get("reviews.Username");




        newReview.put("id",reviews_id);
        newReview.put("date",reviewDate);
        newReview.put("doRaccomend",reviewDoRaccomend);
        newReview.put("rating",reviewRating);
        newReview.put("text",reviewText);
        newReview.put("title",reviewTitle);
        newReview.put("username",reviewUsername);


    }
}