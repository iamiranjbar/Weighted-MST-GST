package misc;

public class WaitForConnect {
    private String node;
    private int level;

    public WaitForConnect(String node, int level) {
        this.node = node;
        this.level = level;
    }

    public String getNode() {
        return node;
    }

    public int getLevel() {
        return level;
    }
}
