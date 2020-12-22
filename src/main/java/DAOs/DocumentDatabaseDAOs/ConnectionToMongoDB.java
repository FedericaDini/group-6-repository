package DAOs.DocumentDatabaseDAOs;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


public class ConnectionToMongoDB {

    private MongoClient mongo = null;
    private MongoDatabase database = null;

    public MongoDatabase getMongoDatabase() {
        return database;
    }

    public void openConnection(String uri) {

        // Creating a Mongo client
        ConnectionString connectionString = new ConnectionString(uri);
        mongo = MongoClients.create(connectionString);

        // Accessing the database
        database = mongo.getDatabase("e-shop");
    }

    /* //Retrieving the collections

        MongoCollection<Document> reviewsColl = database.getCollection("reviews");
        MongoCollection<Document> usersColl = database.getCollection("users");
        MongoCollection<Document> ordersColl = database.getCollection("orders");*/

    public boolean isNewElement(MongoCollection<Document> collection, String id) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);
        MongoCursor<Document> cursor = collection.find(whereQuery).iterator();
        if (cursor.hasNext()) {
            return false;
        } else {
            return true;
        }
    }


    public void closeConnection() {
        mongo.close();
    }

    //JSONObject newObj=new JSONObject();
    //JSONParser parser = new JSONParser();
    //DBObject obj2=null;
    //MongoCursor<Document> cur = collection.find().iterator()
      /*  try {
            /*while (cur.hasNext()) {

                var doc = cur.next();
                var roba = new ArrayList<>(doc.values());
                System.out.println( roba.get(1));
                System.out.println( roba.get(2));
                System.out.println( roba.get(3));
                System.out.println( roba.get(4));
            }
            try {
                File myObj = new File("C:/Users/Giovanni/Desktop/newDocument.json");
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            MongoCursor<String> it = collection.distinct("id", String.class).iterator();
            MongoCursor<Document> it2 = null;
            List<Document> docs = new ArrayList<Document>();
            FileWriter myWriter = new FileWriter("C:/Users/Giovanni/Desktop/newDocument.json");
            while (it.hasNext()) {
                Bson bsonFilter = Filters.eq("id", it.next());
                it2 = collection.find(bsonFilter).limit(1).iterator();
                Document d = it2.next();
                Random rd = new Random();
                double min = 1;
                double max = 500;
                double random = new Random().nextDouble();
                double result = min + (random * (max - min));
                Document d2 = new Document().append("id", d.getString("id")).append("name", d.getString("name")).append("brand", d.getString("brand")).append("mainCategory", d.getString("primaryCategories")).append("categories", d.get("categories")).append("price", String.format("%.2f", result));
                try {

                    myWriter.write(d2.toJson() + "," + "\n");

                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
            myWriter.close();




            /*while (it2.hasNext()) {
                docs.add(it2.next());
            }
            for (int i = 0; i < docs.size(); i++) {

            }
            //
            //it.into(docs);

            /*for (int i=0;i<docs.size();i ++) {
                System.out.println(docs.get(i).get("id"));
            }
            //List<Document> docs = new ArrayList<>();
            //Object obj =  parser.parse(new FileReader("C:/Users/Giovanni/Desktop/aaaaa.json"));
            //Document document = Document.parse(String.format("{\"a\": %s}", obj.toString()).replace("reviews.title","reviewsTitle").replace("reviews.dateAdded","reviewsDateAdded").replace("reviews.id","reviewsId").replace("reviews.doRecommend","reviewsDoRecommend").replace("reviews.date","reviewsDate").replace("reviews.numHelpful","reviewsNumHelpful").replace("reviews.sourceURLs","reviewsSourceURLs").replace("reviews.rating","reviewsRating").replace("reviews.text","reviewsText").replace("reviews.username","reviewsUsername"));
            //collection.insertOne(document);
            //Document dbObject2 = Document.parse(obj.toString());
            //docs.add(dbObject2);
            //Object obj3 = parser.parse(new FileReader("C:/Users/Giovanni/Desktop/aaaaa.json"));
            //JSONArray lista = (JSONArray) obj;

            //BasicDBObject dbObject = com.mongodb.BasicDBObject.parse(lista.toString());
            //array.add(obj);
            //obj2=(DBObject) dbObject;
        } catch (Exception e) {
            e.printStackTrace();
        }


        //List<DBObject> lists = new ArrayList<DBObject>();

        //lists.add(obj2);


    }
    */

}