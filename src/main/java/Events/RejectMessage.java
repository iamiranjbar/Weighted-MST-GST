package Events;

import se.sics.kompics.KompicsEvent;

public class RejectMessage implements KompicsEvent {
    private String source, destination;

    public RejectMessage(String source, String destination) {
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
        return "Reject: { From: " + source + ", To: " + destination + "}";
    }
}
