package misc;

import java.util.Objects;

public class MSTEdge {
    private String source, destination;
    private int weight;

    public MSTEdge(String source, String destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return this.source + "-" + this.destination + "," + this.weight;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        MSTEdge otherEdge = (MSTEdge) object;
        return this.weight == otherEdge.weight &&
                ((this.source.equalsIgnoreCase(otherEdge.source) && this.destination.equalsIgnoreCase(otherEdge.destination)) ||
                (this.source.equalsIgnoreCase(otherEdge.destination) && this.destination.equalsIgnoreCase(otherEdge.source)));
    }
}
