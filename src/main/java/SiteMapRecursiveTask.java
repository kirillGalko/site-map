import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveAction;
import static java.lang.Thread.sleep;

public class SiteMapRecursiveTask extends RecursiveAction {
    private SiteMapNode url;
    private SiteMapNode rootUrl;
    private static CopyOnWriteArrayList<String> allLinks = new CopyOnWriteArrayList<>();


    public SiteMapRecursiveTask(SiteMapNode url) {
        this.url = url;
    }

    public SiteMapRecursiveTask(SiteMapNode url, SiteMapNode rootUrl) {
        this.url = url;
        this.rootUrl = rootUrl;
    }


    @Override
    protected void compute() {
        List<SiteMapRecursiveTask> taskList = new ArrayList<>();
        try {
            sleep(500);
            Document doc = Jsoup.connect(url.getUrl()).timeout(100000).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrect(absUrl)) {
                    url.addSublink(new SiteMapNode(absUrl));
                    allLinks.add(absUrl);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        for (SiteMapNode link : url.getSublinks()) {
            SiteMapRecursiveTask task = new SiteMapRecursiveTask(link, rootUrl);
            task.fork();
            taskList.add(task);
        }

        for (SiteMapRecursiveTask task : taskList) {
            task.join();
        }
    }

    private boolean isCorrect(String url) {
        return (!url.isEmpty() && url.startsWith(rootUrl.getUrl())
                && !allLinks.contains(url) && !url.contains("#")
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"));
    }
}
