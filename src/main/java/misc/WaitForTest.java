package misc;

public class WaitForTest {
    private String node;
    private int level;
    private int fragment;

    public WaitForTest(String node, int level, int fragment) {
        this.node = node;
        this.level = level;
        this.fragment = fragment;
    }

    public String getNode() {
        return node;
    }

    public int getLevel() {
        return level;
    }

    public int getFragment() {
        return fragment;
    }
}
