public class Main {
    public static void main(String[] args) {
        Downloader d = new Downloader("http://www.keenthemes.com/preview/metronic/templates/admin/","index.html","D:/Projects/Sources/Themes/");
        d.Download();
        d.getOtherLinks();
    }
}
