import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Downloader {

    private String baseUrl;
    private String fullUrl;
    private String outputDirectory;
    private Document doc;

    private static Set<String> downloadedPages = new HashSet<>();
    private static Set<String> downloadedResources = new HashSet<>();
    private static Set<String> otherLinks = new HashSet<>();

    public Downloader(String baseUrl, String file, String outputDirectory) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.fullUrl = optimizeUrl(file.startsWith("/") ? file.substring(1) : file);
        this.outputDirectory = outputDirectory;
    }


    public void Download() {
        String link = fullUrl;
        try {
            doc = Jsoup.connect(link).get();
            printMessage("****Processing page: " + fullUrl + "****");
        } catch (IOException e) {
            printMessage("Url is not a page: " + fullUrl + ". Trying downloading...");
            DownloadFileFromUrl(link);
            return;
        }
        GetResources("link", "href");
        GetResources("script", "src");
        GetResources("img", "src");
        LoadPage();
    }

    private void GetResources(String tag, String attribute) {
        Elements elements = doc.select(tag);
        if (elements == null)
            return;
        for (Element element : elements) {
            String link = element.attr(attribute);
            DownloadFileFromUrl(link);
        }
    }

    private void LoadPage() {
        DownloadFileFromUrl(fullUrl);
        downloadedPages.add(fullUrl);
        Elements elements = doc.select("a");
        if (elements == null)
            return;
        for (Element element : elements) {
            String link = optimizeUrl(element.attr("href"));
            if (!link.startsWith(baseUrl)) {
                otherLinks.add(link);
                continue;
            }
            if (!link.contains("#") && !downloadedPages.contains(link)) {
                Downloader d = new Downloader(baseUrl, link.substring(baseUrl.length()), outputDirectory);
                d.Download();
            }
        }
    }

    private void DownloadFileFromUrl(String link) {
        link = optimizeUrl(link);
        if (downloadedResources.contains(link))
            return;
        downloadedResources.add(link);
        try {
            URL url = new URL(link);
            FileUtils.copyURLToFile(url, new File(outputDirectory + url.getFile()));
            printMessage("File downloaded: " + link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            printMessage("File is not a valid resource: " + link);
        }
    }

    private String optimizeUrl(String url) {
        if (url.contains("../")) {
            try {
                URL mergedURL = new URL(new URL(baseUrl), url);
                url = mergedURL.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (!url.startsWith("http")) {
            url = baseUrl + url;
        }
        return url;
    }

    protected void printMessage(String s) {
        System.out.println(s);
    }

    public void getOtherLinks(){
        printMessage("Other links: ");
        for(String s:otherLinks){
            printMessage(s);
        }
    }

}
