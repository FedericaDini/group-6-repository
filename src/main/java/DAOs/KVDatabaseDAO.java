package DAOs;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import beans.Product;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class KVDatabaseDAO {
    private static DB db = null;

    public void openDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File("E-Shop-local-cart"), options);
        } catch (IOException ioe) {
            closeDB();
        }
    }

    public void closeDB() {
        try {
            if (db != null) {
                db.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void putValue(String key, String string) {
        db.put(bytes(key), bytes(string));
    }

    public String getValue(String key) {
        return asString(db.get(bytes(key)));
    }

    public void deleteValue(String key) {
        db.delete(bytes(key));
    }

    public HashMap<String, String> getProductsByUsername(String username) {

        DBIterator i = db.iterator();

        HashMap<String, String> l = new HashMap<>();

        String prodName = null;
        String prodPrice = null;
        String prodQuantity = null;

        try {

            String actualProduct = null;

            for (i.seek(bytes(username)); i.hasNext(); i.next()) {

                String key = asString(i.peekNext().getKey());
                String[] split = key.split(":");

                if (!split[0].equals(username)) {
                    break;
                }

                if (actualProduct == null) {
                    actualProduct = split[1];
                    prodName = asString(db.get(bytes(key)));
                }

                if (actualProduct.equals(split[1])) {
                    if (split[2].equals("price")) {
                        prodPrice = asString(db.get(bytes(key)));
                    }
                    if (split[2].equals("quantity")) {
                        prodQuantity = asString(db.get(bytes(key)));
                    }
                } else {
                    l.put(actualProduct, prodName + " (" + prodPrice + " x " + prodQuantity + ")");
                    actualProduct = split[1];
                    prodName = asString(db.get(bytes(key)));
                }
            }
        } finally {
            try {
                i.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return l;

      /*  HashMap<String, String> map = new HashMap<>();
        map.put("oo1", "p1");
        map.put("oo2", "p2");
        map.put("oo3", "p3");
        map.put("oo4", "p4");
        map.put("oo5", "p5");
        map.put("oo6", "p6");
        return map;*/
    }

    //Removes the product from the cart of a specific user
    public void removeProductFromCart(String product, String username) {
        deleteValue(username + ":" + product + ":" + "name");
        deleteValue(username + ":" + product + ":" + "quantity");
        deleteValue(username + ":" + product + ":" + "price");
    }

    //Removes the cart of the deleted user
    public void removeUserFromCart(String username) {
        DBIterator i = db.iterator();

        for (i.seek(bytes(username)); i.hasNext(); i.next()) {
            String key = asString(i.peekNext().getKey());
            String[] split = key.split(":");

            if (split[0].equals(username)) {
                deleteValue(key);
            } else {
                break;
            }
        }
    }

    public void insertProductToCart(Product p, String username, int quantity) {

        String actualQuantity = getValue(username + ":" + p.getId() + ":" + "quantity");
        if (actualQuantity.isEmpty()) {
            putValue(username + ":" + p.getId() + ":" + "name", p.getName());
            putValue(username + ":" + p.getId() + ":" + "price", String.valueOf(p.getPrice()));
            putValue(username + ":" + p.getId() + ":" + "quantity", String.valueOf(quantity));
        } else {
            int q = Integer.parseInt(actualQuantity);
            q = q + quantity;
            putValue(username + ":" + p.getId() + ":" + "quantity", String.valueOf(q));
        }
    }
}
