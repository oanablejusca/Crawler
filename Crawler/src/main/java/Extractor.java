import model.Product;
import model.Review;
import org.apache.tika.language.LanguageIdentifier;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class Extractor {

    public static Product extractProductInfo(WebDriver driver) {

        String priceSelector = "#centerCtrl > div.itemBox > div.item_price_box > div.item_now_price";
        String price = driver.findElement(By.cssSelector(priceSelector)).getText();
        while(price == null || price.length() == 0) {
            price = driver.findElement(By.cssSelector(priceSelector)).getText();
        }
        String title = driver.findElement(By.className("title_strong")).getText();
        String starSelector = "#centerCtrl > div.centerCtrlInfo > ul > li.item_review.clearfix > div > span.star > i";
        String scoreText = driver.findElement(By.cssSelector(starSelector)).getAttribute("style");
        double score = Double.parseDouble(scoreText.substring(6, scoreText.length() - 2));

        return new Product(title, score, price);
    }


    public static Review extractReviewInfo(WebElement reviewItem) {
        // extract comment
        try {
            String comment = reviewItem.findElement(By.cssSelector("div.rev_item_right > div.rev_info.clearfix > p.rev_txt")).getText();
            if(comment.length() < 1) {
                return null;
            } else {
                // Filter reviews that are not in English
                LanguageIdentifier lang = new LanguageIdentifier(comment);
                if(!lang.getLanguage().equals("en")) {
                    return null;
                } else {
                    // extract user name
                    String userName=reviewItem.findElement(By.className("reviewer_name")).getText();

                    // extract score
                    String starSelector ="div.rev_item_right > div.rev_info.clearfix > div.title.clearfix > div > i.rev_score_star.rev_score_star_small.fl > i";
                    String scoreText = reviewItem.findElement(By.cssSelector(starSelector)).getAttribute("style");
                    double score = Double.parseDouble(scoreText.substring(7, scoreText.length() - 2));

                    // extract date
                    String date=reviewItem.findElement(By.className("rev_time")).getText();
                    DateFormat fd = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    Date reviewDate = null;
                    try {
                        reviewDate = fd.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return new Review(reviewDate, userName, comment, score);
                }
            }
        }catch (NoSuchElementException e) {
            System.out.println("Text not found for given review");
            return null;
        }
    }

    public static List<String> fetchRecommendedProducts(WebDriver driver, String urlPattern) {
        List<String> results = new LinkedList<String>();
        WebElement element = driver.findElement(By.id("HotProducts"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        while(true){
            try {
                List<WebElement> firstRec = driver.findElements(By.xpath("//*[@id=\"HotProducts\"]/li[1]/span[2]/a"));
                if(firstRec.size() > 0)
                    break;
            }catch (NoSuchElementException e) {
                continue;
            }
        }

        try {
            List<WebElement> elements = null;
            if (urlPattern.startsWith("#")) {
                // find by Id
                elements = driver.findElements(By.id(urlPattern.substring(1)));

            } else if (urlPattern.startsWith(".")) {
                //find by class
                elements = driver.findElements(By.className(urlPattern.substring(1)));

            } else if (urlPattern.startsWith("?")) {
                // find by css selector
                String cssSelector = urlPattern.substring(1);
                elements = driver.findElements(By.cssSelector(cssSelector));

            } else {
                //find by element xpath
                elements = driver.findElements(By.xpath(urlPattern));

            }
            if (elements != null) {
                for (WebElement el : elements) {
                    String url = el.getAttribute("href");
                    if (url.length() > 0) {
                        results.add(url);
                    }
                }
            }
            return results;

        } catch (NoSuchElementException e) {
            System.out.println("No match for selector " + urlPattern);
            return null;
        }
    }
    public  static  List<Review>  getReviews(WebDriver driver, String reviewPattern) {
        List<Review> results= new LinkedList<>();

        WebElement element = driver.findElement(By.className("tabbar_wrap"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        String style = element.findElement(By.cssSelector("#jspromsgwrap > div.tabbar_wrap > ul > li.tabbar_item.none.jsReviewItem")).getAttribute("style");
        if(style.equals("display: none;")) {
            return null;
        }
        WebElement reviewButton = driver.findElement(By.xpath("//*[@id=\"jspromsgwrap\"]/div[2]/ul/li[3]/a"));
        reviewButton.click();
        WebElement lastButton = null;

        while(true) {
            try {
                lastButton = driver.findElement(By.cssSelector("#jsreviewsListWrap > div.box > div > div > div > div.rate_paginator > a:nth-last-child(2)"));
                if(lastButton != null) {
                    break;
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                try {
                    // if the button can't be found, the product may have 5 or less reviews
                    WebElement ratePagin = driver.findElement(By.cssSelector("#jsreviewsListWrap > div.box > div > div > div > div.rate_paginator"));
                    List<WebElement> children = ratePagin.findElements(By.xpath(".//*"));
                    if(children.size() <= 5) {
                        break;
                    }
                } catch(org.openqa.selenium.NoSuchElementException ex) {
                    // use this to cycle till the elements ar eloaded
                    continue;
                }
            }
        }

        if( lastButton != null) {
            int numPages = Integer.parseInt(lastButton.getAttribute("innerHTML"));

            String nextButtonSelector = "rate_page_next_icon";

            for (int i = 0; i < numPages; i++) {
                // extract the information on the current page
                List<WebElement> elements = driver.findElements(By.className("rev_item"));
                for (WebElement e : elements) {
                    Review r = Extractor.extractReviewInfo(e);
                    if (r != null) {
                        results.add(r);
                    }
                }
                System.out.println("Elements found: " + elements.size());
                //click the arrow
                WebElement arrowButton = driver.findElement(By.className(nextButtonSelector));
                Actions actions = new Actions(driver);
                actions.moveToElement(arrowButton).click().perform();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //extract info - same as in for
        List<WebElement> elements = driver.findElements(By.className("rev_item"));
        for(WebElement e: elements) {
            Review r = Extractor.extractReviewInfo(e);
            if(r != null) {
                results.add(r);
            }
        }
        return results;
    }

}
