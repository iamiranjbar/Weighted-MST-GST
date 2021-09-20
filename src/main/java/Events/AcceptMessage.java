package Events;

import se.sics.kompics.KompicsEvent;

public class AcceptMessage implements KompicsEvent {
    private String source, destination;

    public AcceptMessage(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Accept: { From: " + source + ", To: " + destination + "}";
    }
}
