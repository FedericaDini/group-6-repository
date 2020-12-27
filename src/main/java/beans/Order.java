package beans;

import DAOs.DocumentDatabaseDAOs.ProductDAO;
import utilities.Types.OrderState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Order {
    private String id;
    private Date date;
    private HashMap<String, Integer> products;
    private Double totalAmount;
    private OrderState state;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Integer> products) {
        this.products = products;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Order(HashMap<String, Integer> products, Double totalAmount, String userId, OrderState state) {
        this.products = products;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.state = state;
        this.date = new Date();
    }

    public Order(String id, HashMap<String, Integer> products, Double totalAmount, String userId, OrderState state) {
        this.id = id;
        this.products = products;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.state = state;
        this.date = new Date();
    }

    @Override
    public String toString() {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String s = "Id: " + id + "\n" +
                "Date: " + simpleDateFormat.format(date) + "\n" +
                "Total amount: " + Math.round(totalAmount * 100.0) / 100.0 + "\n" +
                "State: " + state + "\n" +
                "Products:\n";

        for (String key : products.keySet()) {
            s = s.concat("productID: " + key + " (quantity: " + products.get(key) + ")\n");
        }

        return s;
    }
}
