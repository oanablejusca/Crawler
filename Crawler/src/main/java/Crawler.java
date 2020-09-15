import model.Product;
import model.Review;
import org.bson.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Crawler extends Thread {
    public String siteUrl;
    public String urlPatternCrawl;
    public String commentsPattern;
    public  String getUrlPatternExtraction;
    private Queue<String> urlQ;
    private DBManager DBManager;

    public Crawler(String siteUrl, String urlPatternCrawl, String commentsPattern) {
        this.siteUrl = siteUrl;
        this.urlPatternCrawl = urlPatternCrawl;
        this.DBManager = DBManager.getDBManager();
        this.commentsPattern = commentsPattern;
        urlQ = new LinkedList<String>();
        if (this.DBManager.isVisited(siteUrl) == false)
            urlQ.add(siteUrl);
        else
            System.out.println("The link provided is already visited");
    }

    public String getSiteUrl() {
        return siteUrl;
    }
    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }
    public String getUrlPatternCrawl() {
        return urlPatternCrawl;
    }
    public void setUrlPatternCrawl(String urlPatternCrawl) {
        this.urlPatternCrawl = urlPatternCrawl;
    }
    public String getGetUrlPatternExtraction() {
        return getUrlPatternExtraction;
    }
    public void setGetUrlPatternExtraction(String getUrlPatternExtraction) {
        this.getUrlPatternExtraction = getUrlPatternExtraction;
    }

    private List<String> checkUrls(List<String> listUrl) throws IndexOutOfBoundsException {
        if (listUrl == null) {
            throw new IndexOutOfBoundsException();
        }
        List<String> urlsList = new LinkedList<String>();
        for (String url : listUrl) {
            if (DBManager.isVisited(url) == false)
                urlsList.add(url);
        }
        return urlsList;
    }

    @Override
    public void run() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        String webDriverKey = "webdriver.chrome.driver";
        System.setProperty(webDriverKey, "E:\\chromedriver_win32\\chromedriver.exe");

        WebDriver driver;
        driver = new ChromeDriver(options);

        while (true) {
            if (urlQ.isEmpty()) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                String url = urlQ.remove();
                this.DBManager.setUrlAsVisited(url);
                driver.get(url);
                try {
                    Product product = Extractor.extractProductInfo(driver);

                    List<String> recommended = Extractor.fetchRecommendedProducts(driver, this.urlPatternCrawl);
                    List<String> urls = this.checkUrls(recommended);

                    List<Review> reviews = Extractor.getReviews(driver, this.commentsPattern);
                    Document productDocument = Utils.createBsonProduct(url, product, reviews, urls);
                    this.DBManager.insertProductInDatabase(productDocument);
                    System.out.println("DEBUG");
                    for (String u : urls) {
                        urlQ.add(u);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("No URLS found for url: " + url);
                }
            }
        }
    }
}
