package DAOs.DocumentDatabaseDAOs;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Indexes;
import org.bson.Document;


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

        //Creating the indexes if not exist
        createIndexes();

        //Set the configurations for the clustered database
        setConfigurations();
    }

    public boolean isNewID(MongoCollection<Document> collection, String id) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("_id", id);
        MongoCursor<Document> cursor = collection.find(whereQuery).iterator();
        return !cursor.hasNext();
    }

    public void setConfigurations(){

    }

    public void createIndexes() {

        //To speed up the product search by key words
        database.getCollection("products").createIndex(Indexes.text("name"));

        //To speed up the computation when searching a user by username
        database.getCollection("users").createIndex(Indexes.ascending("username"));

        //To speed up the computation when searching an order by username
        database.getCollection("orders").createIndex(Indexes.ascending("user"));
    }

    public void closeConnection() {
        mongo.close();
    }
}