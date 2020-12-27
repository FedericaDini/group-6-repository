package DAOs;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import beans.Product;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class KVDatabaseDAO {

    private static DB db = null;

    public void openDB() {
        try {
            db = factory.open(new File("e-shop-cart"), new Options().createIfMissing(true));
        } catch (IOException e) {
            e.printStackTrace();
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

        HashMap<String, String> l = new HashMap<>();

        String prodName = null;
        String prodPrice = null;
        String prodQuantity = null;

        try (DBIterator i = db.iterator()) {

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
                        l.put(actualProduct, prodName + " --> " + prodPrice + " x " + prodQuantity);
                        actualProduct = null;
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return l;
    }

    //Removes the product from the cart of a specific user
    public void removeProductFromCart(String product, String username) {

        WriteBatch batch = db.createWriteBatch();
        batch.delete(bytes(username + ":" + product + ":" + "name"));
        batch.delete(bytes(username + ":" + product + ":" + "quantity"));
        batch.delete(bytes(username + ":" + product + ":" + "price"));
        db.write(batch);

        System.out.println("DONE." + "\n");

        try {
            batch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Removes the cart of the deleted user
    public void removeUserFromCart(String username) {
        try (DBIterator i = db.iterator()) {
            for (i.seek(bytes(username)); i.hasNext(); i.next()) {
                String key = asString(i.peekNext().getKey());
                String[] split = key.split(":");

                if (split[0].equals(username)) {
                    deleteValue(key);
                } else {
                    break;
                }
            }

            i.close();

            System.out.println("DONE." + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertProductToCart(Product p, String username, int quantity) {

        String actualQuantity = getValue(username + ":" + p.getId() + ":" + "quantity");
        if (actualQuantity == null) {
            WriteBatch batch = db.createWriteBatch();
            batch.put(bytes(username + ":" + p.getId() + ":" + "name"), bytes(p.getName()));
            batch.put(bytes(username + ":" + p.getId() + ":" + "price"), bytes(String.valueOf(p.getPrice())));
            batch.put(bytes(username + ":" + p.getId() + ":" + "quantity"), bytes(String.valueOf(quantity)));
            db.write(batch);
            try {
                batch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int q = Integer.parseInt(actualQuantity);
            q = q + quantity;
            putValue(username + ":" + p.getId() + ":" + "quantity", String.valueOf(q));
        }

        System.out.println("DONE." + "\n");
    }

    public void updateQuantityOfProduct(String id, String username, int q) {
        putValue(username + ":" + id + ":" + "quantity", String.valueOf(q));
        System.out.println("DONE." + "\n");

    }
}
