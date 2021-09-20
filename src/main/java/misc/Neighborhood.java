package misc;

import misc.Enums.EdgeStatus;

public class Neighborhood {
    private int weight;
    private EdgeStatus status;

    public Neighborhood(int weight) {
        this.weight = weight;
        this.status = EdgeStatus.BASIC;
    }

    public void setStatus(EdgeStatus status) {
        this.status = status;
    }

    public int getWeight() {
        return weight;
    }

    public EdgeStatus getStatus() {
        return status;
    }
}
