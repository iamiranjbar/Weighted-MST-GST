package Events;

import misc.Enums.State;
import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    private String source, destination;
    private int fragment, level;
    private State state;

    public InitiateMessage(String source, String destination, int fragment, int level, State state) {
        this.source = source;
        this.destination = destination;
        this.fragment = fragment;
        this.level = level;
        this.state = state;
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

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Initiate: { From: " + source + ", To: " + destination + ", fragment: " + fragment + ", level: " + level +
                ", State: " + state + "}";
    }
}
