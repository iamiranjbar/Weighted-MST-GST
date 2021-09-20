package Events;


import se.sics.kompics.KompicsEvent;

public class ConnectMessage implements KompicsEvent {
    private String source, destination;
    private int level;

    public ConnectMessage(String source, String destination, int level) {
        this.source = source;
        this.destination = destination;
        this.level = level;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Connect: { From: " + source + ", To: " + destination + ", Level: " + level + "}";
    }
}
