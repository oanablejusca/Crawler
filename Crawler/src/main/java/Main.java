
public class Main {
     public static void main(String[] args) {
         String urlPattern= "?#HotProducts > li > .title > a";
         String reviewPattern=".rev_list > li ";
         String seedUrl="https://www.banggood.com/BlitzWolf-BW-ES4-Dual-Dynamic-Drivers-Graphene-Earphone-3_5mm-Wired-Control-In-ear-Earbuds-Magnetic-Headphone-with-Mic-p-1612613.html?rmmds=category&ID=224&cur_warehouse=CN";
         Crawler crawler = new Crawler(seedUrl, urlPattern, reviewPattern);
         crawler.start();
    }
}
