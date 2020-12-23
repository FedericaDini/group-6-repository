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

    public boolean isNewElement(MongoCollection<Document> collection, String id) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
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

      //MongoCursor<String> it = collection.distinct("id", String.class).iterator();
}