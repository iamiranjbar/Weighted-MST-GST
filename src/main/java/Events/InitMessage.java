package Events;

import Components.Node;
import misc.Neighborhood;
import se.sics.kompics.Init;

import java.util.HashMap;

public class InitMessage extends Init<Node> {
    private String nodeName;
    private HashMap<String, Neighborhood> neighbours;

    public InitMessage(String nodeName, HashMap<String, Neighborhood> neighbours) {
        this.nodeName = nodeName;
        this.neighbours = neighbours;
    }

    public String getNodeName() {
        return nodeName;
    }

    public HashMap<String, Neighborhood> getNeighbours() {
        return neighbours;
    }
}