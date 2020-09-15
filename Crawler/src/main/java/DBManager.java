import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private MongoClient client;
    private MongoDatabase db = null;
    private static DBManager manager = null;

    private DBManager() {
        client = new MongoClient("localhost", 27017);
        db = client.getDatabase("licenta");
    }

    public static DBManager getDBManager() {
        if (manager != null) {
            return manager;
        } else {
            synchronized (DBManager.class) {
                if (manager == null)
                    manager = new DBManager();
            }
            return manager;
        }
    }

    public Boolean isVisited(String url) {
        MongoCollection<Document> collection = db.getCollection("visitedlinks");
        MongoCursor cursor = collection.find(Filters.eq("url", url)).iterator();
        return cursor.hasNext();
    }

    public void setUrlAsVisited(String url) {
        MongoCollection<Document> collection = db.getCollection("visitedlinks");
        Map<String, Object> documentMap = new HashMap<String, Object>();
        documentMap.put("url", url);
        collection.insertOne(new Document(documentMap));
    }

    public void insertProductInDatabase(Document productDocument) {
        MongoCollection<Document> collection = db.getCollection("product");
        collection.insertOne(productDocument);
    }
}
