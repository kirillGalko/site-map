import java.util.concurrent.CopyOnWriteArrayList;


public class SiteMapNode {
    private String url;
    private volatile SiteMapNode parent;
    private volatile int depth;
    private volatile CopyOnWriteArrayList<SiteMapNode> sublinks;

    public SiteMapNode(String url) {
        this.url = url;
        sublinks = new CopyOnWriteArrayList<>();
        depth = 0;
        parent = null;

    }

    public void addSublink(SiteMapNode sublink) {
        if (!sublinks.contains(sublink) && sublink.getUrl().startsWith(url)) {
            sublinks.add(sublink);
            sublink.setParent(this);
        }
    }

    private void setParent(SiteMapNode siteMapNode) {
        synchronized (this) {
            this.parent = siteMapNode;
            this.depth = setDepth();
        }
    }

    public int getDepth() {
        return depth;
    }

    private int setDepth() {
        if (parent == null) {
            return 0;
        }
        return 1 + parent.getDepth();
    }


    public CopyOnWriteArrayList<SiteMapNode> getSublinks() {
        return sublinks;
    }

    public String getUrl() {
        return url;
    }
}
