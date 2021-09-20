package Events;

import se.sics.kompics.KompicsEvent;

public class ReportMessage implements KompicsEvent {
    private String source, destination;
    private int bestWeight;

    public ReportMessage(String source, String destination, int bestWeight) {
        this.source = source;
        this.destination = destination;
        this.bestWeight = bestWeight;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getBestWeight() {
        return bestWeight;
    }

    @Override
    public String toString() {
        return "Report: { From: " + source + ", To: " + destination + ", Best weight: " + bestWeight + "}";
    }
}
