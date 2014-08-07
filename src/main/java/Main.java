public class Main {
    public static void main(String[] args) {
        Downloader[] downloaders = {
                new Downloader("http://titanicthemes.com/demo/travel/green/", "index.html", "D:/"),
                new Downloader("http://titanicthemes.com/demo/travel/blue/", "index.html", "D:/"),
                new Downloader("http://titanicthemes.com/demo/travel/cyan/", "index.html", "D:/"),
                new Downloader("http://titanicthemes.com/demo/travel/orange/", "index.html", "D:/")
        };
        for (Downloader d : downloaders) {
            d.Download();
            d.getOtherLinks();
        }
    }
}
