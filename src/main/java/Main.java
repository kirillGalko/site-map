import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String SITE = "https://skillbox.ru/";
    private static final String SITEMAP = "src/main/resources/sitemap.txt";

    public static void main(String[] args) {
        SiteMapNode rootUrl = new SiteMapNode(SITE);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new SiteMapRecursiveTask(rootUrl, rootUrl));
        writeSitemapUrl(rootUrl, SITEMAP);
    }

    public static void writeSitemapUrl(SiteMapNode node, String sitemapDoc) {
        int depth = node.getDepth();
        String tabs = String.join("", Collections.nCopies(depth, "\t"));
        StringBuilder result = new StringBuilder(tabs + node.getUrl() + "\n");
        appendStringInFile(sitemapDoc, result.toString());
        node.getSublinks().forEach(link -> writeSitemapUrl(link, sitemapDoc));
    }

    private static void appendStringInFile(String fileName, String data) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName, true);
            outputStream.write(data.getBytes(), 0, data.length());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

