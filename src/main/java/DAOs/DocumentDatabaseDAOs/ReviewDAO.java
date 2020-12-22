package DAOs.DocumentDatabaseDAOs;

import beans.Review;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;

public class ReviewDAO {

    //Enter the database and find all the reviews written about a specific product
    public static ArrayList<Review> findReviewsByProductId(MongoDatabase mongoDB, String id) {

        ArrayList<Review> reviewsList = new ArrayList<>();
        MongoCollection<Document> reviewsColl = mongoDB.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("product", id);
        MongoCursor<Document> cursor = reviewsColl.find(whereQuery).iterator();
        while (cursor.hasNext()) {

            Document d = cursor.next();
            Review r = new Review(d.getString("title"), d.getString("text"), d.getDouble("rate"), d.getBoolean("doRecommend"), d.getString(id), d.getString("username"));
            reviewsList.add(r);
        }
        return reviewsList;
    }

    //Enter the database and find all the reviews written by a specific user
    public static ArrayList<String> findReviewsByUsername(MongoDatabase mongoDB, String username) {

        ArrayList<String> reviewsList = new ArrayList<>();
        MongoCollection<Document> reviewsColl = mongoDB.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("user", username);
        MongoCursor<Document> cursor = reviewsColl.find(whereQuery).iterator();
        while (cursor.hasNext()) {

            Document d = cursor.next();
            String id = d.getString("_id");
            reviewsList.add(id);
        }
        return reviewsList;
    }

    //Enter the database and compute the mean value of the rates for a product
    public static double computeAvgReviewsRateByProductId(MongoDatabase mongoDB, String id) {

        double avg = 0.0;
        int nReviews = 0;
        MongoCollection<Document> reviewsColl = mongoDB.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("product", id);
        MongoCursor<Document> cursor = reviewsColl.find(whereQuery).iterator();
        while (cursor.hasNext()) {

            Document d = cursor.next();
            avg = avg + d.getDouble("rate");
            nReviews++;
        }
        return avg / nReviews;
    }

    //Enter the database and add a review
    public static void addReview(MongoDatabase database, Review review) {

        MongoCollection<Document> reviewsColl = database.getCollection("reviews");

        Document r = new Document("date", review.getDate())
                .append("product", review.getProdId())
                .append("rate", review.getRating())
                .append("text", review.getText())
                .append("title", review.getTitle())
                .append("user", review.getUserId());

        reviewsColl.insertOne(r);
        System.out.println("DONE.");
    }

    //Enter the database and delete all the reviews written by a specific user
    public static void deleteReviewsByUsername(MongoDatabase database, String username) {
        ArrayList<String> reviewsIDs = findReviewsByUsername(database, username);
        Iterator<String> i = reviewsIDs.iterator();
        while (i.hasNext()) {
            String id = i.next();
            deleteReview(database, id);
        }
    }

    //Enter the database and delete a specific review
    public static void deleteReview(MongoDatabase database, String id) {
        MongoCollection<Document> reviewColl = database.getCollection("reviews");

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
        reviewColl.deleteOne(whereQuery);

        System.out.println("DONE.");
    }
}
