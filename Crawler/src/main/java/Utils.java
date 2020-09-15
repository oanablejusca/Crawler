import model.Product;
import model.Review;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public  static String trimUrl(String url){
        if(url.indexOf('#')!=-1){
             int index=url.indexOf('#');
             return  url.substring(0,index);
        }else {
            return url;
        }
    }
    public static Document createBsonProduct(String url, Product product, List<Review> reviews, List<String> connections)
    {
        List<Document> reviewDoc = new ArrayList<>();
        if(reviews != null) {
            for (Review r : reviews) {
                reviewDoc.add(r.toDocument());
            }
        }
        List<Document> connectionsDoc = new ArrayList<>();
        for(String s: connections) {
            connectionsDoc.add(new Document("url", url));
        }

        Document document=new Document();
        document.append("url", url)
                .append("name", product.getTitle())
                .append("scoreAvg", product.getAvgStars())
                .append("price", product.getPrice())
                .append("recommended", connections)
                .append("reviews", reviewDoc);
        return document;
    }
}
