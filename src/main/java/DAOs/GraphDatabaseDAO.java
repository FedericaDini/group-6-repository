package DAOs;

import org.neo4j.driver.*;
import java.util.HashMap;
import java.util.Map;

public class GraphDatabaseDAO {
    private Driver driver = null;
    private Session session = null;

    public GraphDatabaseDAO() {
        try {
            driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1234"));
            session = driver.session();
            System.out.println("good");
        } catch (Exception e) {
            System.out.println("connection error");
        }
    }

    public void close() throws Exception {
        driver.close();
    }

    public void returnAll() {
        Result result = null;
        try {
            result = session.run("MATCH (n) RETURN n.name");
        } catch (Exception e) {
            System.out.println("bad something");
        }
        while (result.hasNext()) {
            System.out.println(result.next().get(0));
        }
    }

    public HashMap<String, String> returnRecommended(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        Result result = null;
        try {
            result = session.run("MATCH(np:person{name:$username})-[*]->(npr:product) WITH collect(npr) as bought,npr.category as lcat match (np2:person)-[*]->(npr2:product) WHERE EXISTS {MATCH (np2)-[:have_purchased]->(npr3:product) where not np2.name=$username and npr3 in bought} and not npr2 in bought and npr2.category in lcat  RETURN npr2.name, npr2.idP", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }

        HashMap<String, String> results = new HashMap<>();

        if (result != null) {
            while (result.hasNext()) {
                results.put(result.next().get(0).asString(), result.next().get(1).asString());
            }
        }

        return results;
    }

    public void returnTopRated() {
        Result result = null;
        try {
            result = session.run("MATCH (x)-[r]->(n:product) RETURN n, COUNT(r) ORDER BY COUNT(r) DESC LIMIT 1");
        } catch (Exception e) {
            System.out.println("bad something");
        }
        while (result.hasNext()) {
            System.out.println(result.next().get(0));
        }
    }

    public void insertPerson(String personName) {
        Map<String, Object> params = new HashMap<>();
        params.put("personName", personName);
        try {
            session.run("MERGE (n:person {name: $personName}) RETURN n", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }

    public void insertProduct(String productName, String productCategory, String idProduct) {
        Map<String, Object> params = new HashMap<>();
        params.put("productName", productName);
        params.put("productCategory", productCategory);
        params.put("idProduct", idProduct);
        try {
            session.run("MERGE (n:product {name: $productName,category: $productCategory, idP: $idProduct}) RETURN n", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }

    public void insertRelationship(String personName, String productName) {
        Map<String, Object> params = new HashMap<>();
        params.put("personName", personName);
        params.put("productName", productName);
        try {
            session.run("MATCH (n:person),(np:product) WHERE n.name=$personName and np.name=$productName  CREATE ((n)-[r:have_purchased{date: date()}]->(np))", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }

    public void deletePerson(String personName) {
        Map<String, Object> params = new HashMap<>();
        params.put("personName", personName);
        try {
            session.run("MATCH (n:person { name:$personName  }) DETACH DELETE n", params);
            session.run("MATCH (n) WHERE NOT (n)--() RETURN n;", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }

    public void deleteProduct(String productName) {
        Map<String, Object> params = new HashMap<>();
        params.put("productName", productName);
        try {
            session.run("MATCH (n:product { name: $productName }) DETACH DELETE n", params);
            session.run("MATCH (n) WHERE NOT (n)--() RETURN n;", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }
}