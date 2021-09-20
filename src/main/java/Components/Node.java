package Components;

import Events.*;
import misc.Enums.EdgeStatus;
import misc.Enums.State;
import Ports.EdgePort;
import misc.*;
import se.sics.kompics.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Node extends ComponentDefinition {
    private static ArrayList<MSTEdge> MST = new ArrayList<>();
    private static Boolean finished = false;

    private Positive<EdgePort> recievePort;
    private Negative<EdgePort> sendPort;
    private String name;
    private State state;
    private HashMap<String, Neighborhood> neighbours;
    private int fragment;
    private int level;
    private int reportedCount;
    private int bestWeight;
    private int parentReport;
    private String parent;
    private String bestEdge;
    private String testEdge;
    private ArrayList<WaitForTest> postponedTests;
    private ArrayList<WaitForConnect> postponedConnects;


    public Node(InitMessage initMessage) {
        this.recievePort = positive(EdgePort.class);
        this.sendPort = negative(EdgePort.class);
        this.name = initMessage.getNodeName();
        this.neighbours = initMessage.getNeighbours();
        this.parent = this.name;
        this.fragment = -1;
        this.level = 0;
        this.state = State.FIND;
        this.postponedConnects = new ArrayList<>();
        this.postponedTests = new ArrayList<>();

        subscribe(startHandler, control);
        subscribe(stopHandler, control);
        subscribe(connectHandler, recievePort);
        subscribe(initiateHandler, recievePort);
        subscribe(testHandler, recievePort);
        subscribe(acceptHandler, recievePort);
        subscribe(rejectHandler, recievePort);
        subscribe(reportHandler, recievePort);
        subscribe(changeRootHandler, recievePort);
    }

    private void addToMST(MSTEdge edge) {
        if (!MST.contains(edge))
            MST.add(edge);
    }

    private String findLeastWightOutgoingEdge() {
        int min = Integer.MAX_VALUE;
        String minNeighborName = null;
        for (HashMap.Entry<String, Neighborhood> neighbor : neighbours.entrySet()) {
            String neighborName = neighbor.getKey();
            Neighborhood neighborhood = neighbor.getValue();
            if (neighborhood.getStatus() != EdgeStatus.BASIC)
                continue;
            if (neighborhood.getWeight() < min) {
                min = neighborhood.getWeight();
                minNeighborName = neighborName;
            }
        }
        return minNeighborName;
    }

    private int getBranchCount() {
        int branchCount = 0;
        for (HashMap.Entry<String, Neighborhood> neighbor : neighbours.entrySet()) {
            String neighborName = neighbor.getKey();
            Neighborhood neighborhood = neighbor.getValue();
            if (neighborhood.getStatus() == EdgeStatus.BRANCH)
                branchCount++;
        }
        return branchCount;
    }

    private void sendMessage(KompicsEvent message) {
        trigger(message, sendPort);
    }

    private void replyTest(String to, int message_fragment) {
        if (fragment != message_fragment) {
            sendMessage(new AcceptMessage(name, to));
        } else {
            Neighborhood neighborhood = neighbours.get(to);
            neighborhood.setStatus(EdgeStatus.REJECTED);
            if (testEdge==null || !testEdge.equalsIgnoreCase(to)) {
                sendMessage(new RejectMessage(name, to));
            } else {
                findMinimalOutgoing();
            }
        }
    }

    private void findMinimalOutgoing() {
        String minNeighborName = findLeastWightOutgoingEdge();
        if (minNeighborName == null) {
            testEdge = null;
        } else {
            sendMessage(new TestMessage(name, minNeighborName, fragment, level));
            testEdge = minNeighborName;
        }
    }

    private void changeRoot() {
        Neighborhood bestNeighbor = neighbours.get(bestEdge);
        if (bestNeighbor.getStatus() == EdgeStatus.BRANCH) {
            sendMessage(new ChangeRootMessage(name, bestEdge));
        } else {
            bestNeighbor.setStatus(EdgeStatus.BRANCH);
            addToMST(new MSTEdge(name, bestEdge, bestNeighbor.getWeight()));
//            System.out.println(name + "-" + bestEdge + " is added to MST!");
            sendMessage(new ConnectMessage(name, bestEdge, level));

            for (WaitForConnect waitedConnect: new ArrayList<>(postponedConnects)) {
                String waitedNode = waitedConnect.getNode();
                int waitedLevel = waitedConnect.getLevel();
                if (bestEdge!=null && bestEdge.equalsIgnoreCase(waitedNode) && level == waitedLevel) {
                    sendMessage(new InitiateMessage(name, bestEdge, bestWeight, level+1, State.FIND));
                    postponedConnects.remove(waitedConnect);
                }
            }
        }
    }

    private void sendReport() {
        state = State.FOUND;
        sendMessage(new ReportMessage(name, parent, bestWeight));
        if (parentReport > 0 && bestWeight < parentReport)
            changeRoot();
    }

    private void terminate() {
        System.out.println("Node " + name + " is terminated!");
    }

    private Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
//            System.out.println(name + ": recieve start!");
            String minNeighborName = findLeastWightOutgoingEdge();
            state = State.FOUND;
            Neighborhood minNeighbor = neighbours.get(minNeighborName);
            minNeighbor.setStatus(EdgeStatus.BRANCH);
            addToMST(new MSTEdge(name, minNeighborName, minNeighbor.getWeight()));
//            System.out.println(name + "-" + minNeighborName + " is added to MST!");
            reportedCount = 1;
            parentReport = 0;

            sendMessage(new ConnectMessage(name, minNeighborName, level));
        }
    };

    private ArrayList<String> getMSTTextOutput() {
        ArrayList<String> result = new ArrayList<>();
        for (MSTEdge edge: MST)
            result.add(edge.toString());
        return result;
    }

    private Handler stopHandler = new Handler<Kill>() {
        @Override
        public void handle(Kill event) {
//            System.out.println(name + ": recieve stop!");
            if (!finished) {
                finished = true;
                Path file = Paths.get("output.txt");
                ArrayList<String> MSTOutput = getMSTTextOutput();
                try {
                    Files.write(file, MSTOutput, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    System.out.println("Can't write in file!");
                }
            }
        }
    };

    private Handler connectHandler = new Handler<ConnectMessage>(){
        @Override
        public void handle(ConnectMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            int receivedLevel = message.getLevel();
            Neighborhood neighborhood = neighbours.get(message.getSource());
            if (receivedLevel < level) {
                sendMessage(new InitiateMessage(message.getDestination(), message.getSource(), fragment, level, state));
                neighborhood.setStatus(EdgeStatus.BRANCH);
                addToMST(new MSTEdge(name, message.getSource(), neighborhood.getWeight()));
//                System.out.println(name + "-" + message.getSource() + " is added to MST!");
            } else if (neighborhood.getStatus() == EdgeStatus.BRANCH) {
                sendMessage(new InitiateMessage(message.getDestination(), message.getSource(), neighborhood.getWeight(),
                        level + 1, State.FIND));
            } else {
                postponedConnects.add(new WaitForConnect(message.getSource(), message.getLevel()));
            }

        }
    };

    private Handler initiateHandler = new Handler<InitiateMessage>(){
        @Override
        public void handle(InitiateMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            fragment = message.getFragment();
            level = message.getLevel();
            state = message.getState();
            parent = message.getSource();
            bestEdge = null;
            bestWeight = Integer.MAX_VALUE;
            reportedCount = 1;
            parentReport = 0;

            for (WaitForConnect waitedConnection: new ArrayList<>(postponedConnects)) {
                String waited = waitedConnection.getNode();
                int waitedLevel = waitedConnection.getLevel();
                Neighborhood waitedNeighbor = neighbours.get(waited);
                if (waitedLevel < level){
                    waitedNeighbor.setStatus(EdgeStatus.BRANCH);
                    addToMST(new MSTEdge(name, waited, waitedNeighbor.getWeight()));
//                    System.out.println(name + "-" + waited + " is added to MST!");
                    postponedConnects.remove(waitedConnection);
                }
            }

            for (HashMap.Entry<String, Neighborhood> neighbor : neighbours.entrySet()) {
                String neighborName = neighbor.getKey();
                Neighborhood neighborhood = neighbor.getValue();
                if (neighborName.equalsIgnoreCase(message.getSource()))
                    continue;
                if (neighborhood.getStatus()==EdgeStatus.BRANCH) {
                    sendMessage(new InitiateMessage(name, neighborName, message.getFragment(), message.getLevel(),
                                message.getState()));
                }
            }

            for (WaitForTest waitedTest: new ArrayList<>(postponedTests)) {
                String waited = waitedTest.getNode();
                int waitedLevel = waitedTest.getLevel();
                int waitedFragment = waitedTest.getFragment();
                Neighborhood waitedNeighbor = neighbours.get(waited);
                if (waitedLevel <= level) {
                    replyTest(waited, message.getFragment());
                    postponedTests.remove(waitedTest);
                }
            }
            if (message.getState() == State.FIND)
                findMinimalOutgoing();
        }
    };

    private Handler testHandler = new Handler<TestMessage>(){
        @Override
        public void handle(TestMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            int receivedLevel = message.getLevel();
            if (receivedLevel <= level) {
                replyTest(message.getSource(), message.getFragment());
            } else {
                postponedTests.add(new WaitForTest(message.getSource(), receivedLevel, message.getFragment()));
            }
        }
    };

    private Handler acceptHandler = new Handler<AcceptMessage>(){
        @Override
        public void handle(AcceptMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            testEdge = null;
            Neighborhood sender = neighbours.get(message.getSource());
            if (sender.getWeight() < bestWeight) {
                bestEdge = message.getSource();
                bestWeight = sender.getWeight();
            }
            if (reportedCount == getBranchCount())
                sendReport();
        }
    };

    private Handler rejectHandler = new Handler<RejectMessage>(){
        @Override
        public void handle(RejectMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            Neighborhood source = neighbours.get(message.getSource());
            source.setStatus(EdgeStatus.REJECTED);
            findMinimalOutgoing();
        }
    };

    private Handler reportHandler = new Handler<ReportMessage>() {
        @Override
        public void handle(ReportMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ":" + message);
            int receivedWeight = message.getBestWeight();
            if (!message.getSource().equalsIgnoreCase(parent)) {
                reportedCount += 1;
                if (receivedWeight < bestWeight) {
                    bestEdge = message.getSource();
                    bestWeight = receivedWeight;
                }
                if (reportedCount == getBranchCount() && testEdge == null)
                    sendReport();
            } else if (state == State.FIND){
                parentReport = receivedWeight;
            } else {
                if (bestWeight < receivedWeight)
                    changeRoot();
                else if (receivedWeight == Integer.MAX_VALUE)
                    terminate();
            }
        }
    };

    private Handler changeRootHandler = new Handler<ChangeRootMessage>() {
        @Override
        public void handle(ChangeRootMessage message) {
            if (!message.getDestination().equalsIgnoreCase(name))
                return;
//            System.out.println(name + ": " + message);
            changeRoot();
        }
    };
}
