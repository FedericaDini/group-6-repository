package beans;

import java.util.ArrayList;

public class Product {
    private String id;
    private String name;
    private String brand;
    private String mainCategory;
    private ArrayList<String> categories;
    private double price;
    private String description;
    private ArrayList<Review> reviewsList;
    private double rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Review> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(ArrayList<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Product() {
    }

    public Product(String id, String name, String brand, String mainCategory, ArrayList<String> categories, double price, String description, ArrayList<Review> reviewsList, double rate) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.mainCategory = mainCategory;
        this.categories = categories;
        this.price = price;
        this.description = description;
        this.rate = rate;
        this.reviewsList = reviewsList;
    }

    public Product(String name, String brand, String mainCategory, ArrayList<String> categories, double price, String description, ArrayList<Review> reviewsList) {
        this.name = name;
        this.brand = brand;
        this.mainCategory = mainCategory;
        this.categories = categories;
        this.price = price;
        this.description = description;
        this.rate = 0.0;
        this.reviewsList = reviewsList;
    }

    @Override
    public String toString() {
        String s = "Id: " + getId() + "\n" +
                "Name: " + getName() + "\n" +
                "Brand: " + getBrand() + "\n" +
                "Main category: " + getMainCategory() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Rate: " + getRate() + "\n";
        return s;

        //sub categories
        //reviews list

    }
}
