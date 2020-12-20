package beans;

import utilities.Types.OrderState;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private String id;
    private Date date;
    private ArrayList<Product> products;
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

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
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

    public Order(ArrayList<Product> products, Double totalAmount, String userId, OrderState state) {
        this.products = products;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.userId = userId;
        this.state = state;
    }

    @Override
    public String toString() {
        String s = "Id: " + getId() + "\n" +
                "Date: " + getDate() + "\n" +
                "Total amount: " + getTotalAmount() + "\n" +
                "State: " + getState() + "\n";
        return s;

        //products list

    }
}
