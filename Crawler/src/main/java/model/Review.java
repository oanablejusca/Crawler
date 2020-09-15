package model;
import org.bson.Document;
import java.util.Date;

public class Review {
   private Date date;
   private String userName;
   private String content;
   private Double stars;

   public Review(Date date, String userName, String content, Double stars) {
        this.date = date;
        this.userName = userName;
        this.content = content;
        this.stars = stars;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public Document toDocument() {
        Document d = new Document();
        d.append("date", this.date.toString());
        d.append("username", this.userName);
        d.append("comment", this.content);
        d.append("score", this.stars.toString());
        return d;
    }
}
