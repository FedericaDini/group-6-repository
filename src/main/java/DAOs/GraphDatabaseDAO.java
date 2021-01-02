package DAOs;
import org.neo4j.driver.Driver;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;


import java.util.HashMap;
import java.util.Map;

public class GraphDatabaseDAO {
    private Driver driver=null;
    private Session session=null;
    public GraphDatabaseDAO() {
        try {
            driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic( "neo4j", "1234" ) );
            session = driver.session();
            System.out.println("good");
        } catch (Exception e) {
            System.out.println("connection error");
        }

    }
    public void close() throws Exception
    {
        driver.close();
    }
    public void returnAll() {
        Result result=null;
        try {
            result = session.run( "MATCH (n) RETURN n.name" );
        }catch (Exception e) {
            System.out.println("bad something");
        }
        while ( result.hasNext() )
        {

            System.out.println( result.next().get(0));

        }
    }

    public void returnReccomended(String productName, String category) {
        Map<String,Object> params = new HashMap<>();
        params.put( "productName",  productName);
        params.put( "category",  category);

        Result result = null;
        try {
            result = session.run( "MATCH(np:person)-[*]->(npr:product) WHERE EXISTS {MATCH (np)-[:have_purchased]->(npr2:product{name:$productName})} AND npr.category=$category AND NOT npr.name=$productName RETURN npr.name",params);
        }catch (Exception e) {
            System.out.println("bad something");
        }


        while ( result.hasNext() )
        {

            System.out.println( result.next().get(0));

        }
    }


    public void returnTopRated() {
        Result result=null;
        try {
            result = session.run( "MATCH (x)-[r]->(n:product) RETURN n, COUNT(r) ORDER BY COUNT(r) DESC LIMIT 1" );
        }catch (Exception e) {
            System.out.println("bad something");
        }

        while ( result.hasNext() )
        {
            System.out.println( result.next().get(0));

        }
    }
    public void insertPerson(String personName) {
        Map<String,Object> params = new HashMap<>();
        params.put( "personName",  personName);
        try {
            session.run( "MERGE (n:person {name: $personName}) RETURN n",params);
        }catch (Exception e) {
            System.out.println("bad something");
        }
    }
    public void insertProduct(String productName, String productCategory) {
        Map<String,Object> params = new HashMap<>();
        params.put( "productName",  productName);
        params.put( "productCategory",  productCategory);
        try {
            session.run( "MERGE (n:product {name: $productName,category: $productCategory}) RETURN n",params);
        }catch (Exception e) {
            System.out.println("bad something");
        }
    }
    public void insertRelationship(String personName, String productName) {
        Map<String,Object> params = new HashMap<>();
        params.put( "personName",  personName);
        params.put( "productName",  productName);
        try {
            session.run( "MATCH (n:person),(np:product) WHERE n.name=$personName and np.name=$productName  CREATE ((n)-[r:have_purchased]->(np))",params);
        }catch (Exception e) {
            System.out.println("bad something");
        }
    }
}