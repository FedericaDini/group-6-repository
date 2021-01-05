package DAOs;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphDatabaseDAO {
    private Driver driver = null;
    private Session session = null;

    public GraphDatabaseDAO() {
        try {
            //driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("", ""));
            driver = GraphDatabase.driver("bolt://172.16.3.145:7687", AuthTokens.basic("neo4j", "superneo"));
            session = driver.session();
        } catch (Exception e) {
            System.out.println("connection error");
        }
    }

    public void close() {
        session.close();
        driver.close();
    }

    public HashMap<String, String> returnRecommended(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        Result result = null;
        try {
            result = session.run("MATCH(np:person{name:$username})-[*]->(npr:product) WITH collect(npr) as bought,npr.category as lcat match (np2:person)-[*]->(npr2:product) WHERE EXISTS {MATCH(np2)-[:have_purchased]->(npr3:product) where not np2.name=$username and npr3 in bought} and not npr2 in bought and npr2.category in lcat  RETURN npr2.idP,npr2.name limit 5", params);
        } catch (Exception e) {
            System.out.println("bad something");
        }
        HashMap<String, String> results = new HashMap<>();
        if (result != null) {
            while (result.hasNext()) {
                Record record = result.next();
                results.put(record.get(0).asString(), record.get(1).asString());
            }
        }
        return results;
    }

    public ArrayList<String> returnBestCategoryForMonth() {
        ArrayList<String> list = new ArrayList<>();
        Result result = null;
        try {
            result = session.run("match (x)-[r]->(y) with collect(r.date.month) as month  MATCH (x)-[r]->(n:product) where r.date.month in month with r.date.year as rYear , r.date.month as rMonth , n.category as nCat , COUNT(r) as rSum order by count(r) desc  return rYear, rMonth , max(rSum), collect(nCat)[0]");
        } catch (Exception e) {
            System.out.println("bad something");
        }
        while (result.hasNext()) {
            Record r = result.next();
            String s = r.get(1) + "/" + r.get(0) + " --> " + r.get(3) + " (" + r.get(2) + ")";
            list.add(s);
        }

        return list;
    }

    public ArrayList<String> returnMeanForMonth() {
        ArrayList<String> list = new ArrayList<>();
        Result result = null;
        try {
            result = session.run("match (x)-[r]->(y) with collect(r.date.month) as month  MATCH (x)-[r]->(n:product) where r.date.month in month with r.date.year as rYear , r.date.month as rMonth , n.category as nCat , COUNT(r) as rSum return rYear , nCat, sum(rSum)*1.0 /12 order by rYear desc");
        } catch (Exception e) {
            System.out.println("bad something");
        }
        while (result.hasNext()) {
            Record r = result.next();
            Value mean = r.get(2);
            String s = r.get(0) + " --> " + r.get(1) + " : " + Math.round(mean.asDouble() * 100.0) / 100.0 + " products purchased";
            list.add(s);
        }
        return list;
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
            session.run("MATCH (n) WHERE size((n)--())=0 DELETE (n)");
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }

    public void deleteProduct(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        try {
            session.run("MATCH (n:product { idP: $id }) DETACH DELETE n", params);
            session.run("MATCH (n) WHERE size((n)--())=0 DELETE (n)");
        } catch (Exception e) {
            System.out.println("bad something");
        }
    }
}