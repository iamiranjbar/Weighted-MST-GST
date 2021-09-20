package Events;

import se.sics.kompics.KompicsEvent;

public class TestMessage implements KompicsEvent {
    private String source, destination;
    private int fragment, level;

    public TestMessage(String source, String destination, int fragment, int level) {
        this.source = source;
        this.destination = destination;
        this.fragment = fragment;
        this.level = level;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getFragment() {
        return fragment;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Test: { From: " + source + ", To: " + destination + ", fragment: " + fragment + ", level: " + level + "}";
    }
}
