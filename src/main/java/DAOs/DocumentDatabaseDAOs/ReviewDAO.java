package DAOs.DocumentDatabaseDAOs;

import beans.Review;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class ReviewDAO {
    public static void insertReview(Review r){
        //Method to enter the database and add a review
        //The date has to be added here
        //The ID must be generated randomly by MongoDB
        System.out.println("DONE.");
    }

    public static ArrayList<Review> findReviewsByProductId(MongoDatabase mongoDB, String id){

        ArrayList<Review> reviewsList = new ArrayList<>();
        MongoCollection<Document> reviewsColl = mongoDB.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("product", id);
        MongoCursor<Document> cursor = reviewsColl.find(whereQuery).iterator();
        while (cursor.hasNext()){

            Document d = cursor.next();
            Review r = new Review(d.getString("title"), d.getString("text"), d.getDouble("rate"), d.getBoolean("doRecommend"), d.getString(id), d.getString("username"));
            reviewsList.add(r);
        }
        return reviewsList;
    }

    public static double computeAvgReviewsRateByProductId(MongoDatabase mongoDB, String id){

       double avg = 0.0;
       int nReviews = 0;
        MongoCollection<Document> reviewsColl = mongoDB.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("product", id);
        MongoCursor<Document> cursor = reviewsColl.find(whereQuery).iterator();
        while (cursor.hasNext()){

            Document d = cursor.next();
            avg = avg + d.getDouble("rate");
            nReviews++;
        }
        return avg/nReviews;
    }

}
