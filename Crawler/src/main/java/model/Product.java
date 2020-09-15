package model;

public class Product {
    private String title;
    private double avgStars;
    private String price;

    public Product(String title, double avgStars, String price) {
        this.title = title;
        this.avgStars = avgStars;
        this.price = price;
    }

    public double getAvgStars() {
        return avgStars;
    }

    public void setAvgStars(double avgStars) {
        this.avgStars = avgStars;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
