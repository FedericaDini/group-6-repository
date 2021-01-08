package it.unipi.lsdb.DAOs.DocumentDatabaseDAOs;

import it.unipi.lsdb.beans.Review;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.*;

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

            Review r = new Review(d.getString("title"), d.getString("text"), d.getDouble("rate"), d.getBoolean("doRecommend"), d.getString(id), d.getString("username"), d.getDate("date"));
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
            String id = d.getObjectId("_id").toString();
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
                .append("user", review.getUserId())
                .append("doRecommend", review.isDoRecommend());

        reviewsColl.insertOne(r);

        computeAvgReviewsRateByProductId(database, review.getProdId());

        System.out.println("DONE." + "\n");
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

        System.out.println("DONE." + "\n");
    }

    //Method to find the products that have more than 500 reviews (rates)
    public static LinkedHashMap<String, Integer> findMostRatedProductsIDs(MongoDatabase database) {

        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        MongoCollection<Document> reviewColl = database.getCollection("reviews");

        Bson group = Aggregates.group("$product", Accumulators.sum("count", 1));
        Bson match = Aggregates.match(Filters.gte("count", 500));
        Bson sort = Aggregates.sort(descending("count"));

        Iterator<Document> iterator = reviewColl.aggregate(Arrays.asList(group, match, sort)).iterator();
        while (iterator.hasNext()) {
            Document d = iterator.next();
            result.put(d.getString("_id"), d.getInteger("count"));
        }
        return result;
    }

    //Method to find the ten products with the higher number of recommendations
    public static LinkedHashMap<String, Integer> findMostRecommendedProducts(MongoDatabase database) {

        MongoCollection<Document> reviewColl = database.getCollection("reviews");

        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        Bson match = Aggregates.match(Filters.eq("doRecommend", true));
        Bson group = Aggregates.group("$product", Accumulators.sum("count", 1));
        Bson sort = Aggregates.sort(Sorts.descending("count"));
        Bson limit = Aggregates.limit(10);

        Iterator<Document> iterator = reviewColl.aggregate(Arrays.asList(match, group, sort, limit)).iterator();
        while (iterator.hasNext()) {
            Document d = iterator.next();
            result.put(d.getString("_id"), d.getInteger("count"));
        }
        return result;
    }
}
