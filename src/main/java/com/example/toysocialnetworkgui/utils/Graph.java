package com.example.toysocialnetworkgui.utils;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Friendship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Clasa care implementeaza un graf
 */
public class Graph {
    public static class Path {
        private int len, start, end;

        /**
         * Clasa care implementeaza un lant in graf
         * @param len - lungimea lantului
         * @param start - inceputul lantului
         * @param end - sfarsitul lantului
         */
        public Path(int len, int start, int end) {
            this.len = len;
            this.start = start;
            this.end = end;
        }

        /**
         * Getter pentru lungime
         * @return lungimea lantului
         */
        public int getLen() {
            return len;
        }

        /**
         * Getter pentru inceputul lantului
         * @return inceputul lantului
         */
        public int getStart() {
            return start;
        }

        /**
         * Getter pentru sfarsitul lantului
         * @return sfarsitul lantului
         */
        public int getEnd() {
            return end;
        }
    }

    private int numberOfVertices;
    private LinkedList<Integer>[] adjacencyList;
    private ArrayList<ArrayList<Integer>> allComponents;

    private ArrayList<Integer> theMostSociableCommunity;

    private Controller controller;
    private int maxLen, startNode, endNode;

    /**
     * Constructorul grafului
     * @param controller - controllerul pentru care lucreaza graful
     */
    public Graph(Controller controller) {
        this.controller = controller;
        this.numberOfVertices = getNumberOfVerticies();

        adjacencyList = new LinkedList[numberOfVertices];
        allComponents = new ArrayList<>();
        theMostSociableCommunity = new ArrayList<>();

        for (int i = 0; i < numberOfVertices; i++) {
            adjacencyList[i] = new LinkedList<>();
        }

        populateGraph();

        this.maxLen = -1;
        this.startNode = -1;
        this.endNode = -1;
    }

    /**
     * numarul de noduri din graf
     * @return numarul de noduri din graf
     */
    private int getNumberOfVerticies() {
        return controller.maxId() + 1;
    }

    /**
     * populeaza graful dupa prietenii
     */
    private void populateGraph() {
        for (Friendship friendship : controller.findAllFriendships()) {
            addEdge(Integer.parseInt(friendship.getId().getLeft().toString()), Integer.parseInt(friendship.getId().getRight().toString()));
        }
    }

    /**
     * Adauga o muchie la graf
     * @param n1 - inceput muchie
     * @param n2 - sfarsit muchie
     */
    private void addEdge(int n1, int n2) {
        adjacencyList[n1].add(n2);
        adjacencyList[n2].add(n1);
    }

    /**
     * parcurgere DFS de la un nod al grafului
     * @param node - nodul de la care parcurgem
     * @param visited - vector care contorizeaza ce nod a fost vizitat deja
     * @param components - lista de componenta actuala
     */
    private void DFSUtil(int node, boolean[] visited, ArrayList<Integer> components) {
        visited[node] = true;
        components.add(node);

        Iterator<Integer> it = adjacencyList[node].iterator();

        while (it.hasNext()) {
            int next = it.next();
            if (!visited[next]) {
                DFSUtil(next, visited, components);
            }
        }
    }

    /**
     * parcurgere DFS de la un nod al grafului calculand din lungimile lantului
     * @param src - nodul de la care parcurgem
     * @param prev_len - lungimea precedenta
     * @param visited - vector care contorizeaza ce nod a fost vizitat deja
     * @param components - lista de componenta actuala
     */
    private void DFSUtil2(int src, int prev_len, boolean[] visited, ArrayList<Integer> components) {
        visited[src] = true;
        components.add(src);
        int curr_len = 0;

        Iterator<Integer> it = adjacencyList[src].iterator();

        while (it.hasNext()) {
            int next = it.next();

            if (!visited[next]) {
                curr_len = prev_len + 1;
                DFSUtil2(next, curr_len, visited, components);
            }

            if (curr_len > maxLen) {
                maxLen = curr_len;
                endNode = next;
            }

            curr_len = 0;
        }
    }

    /**
     * Calculeaza cel mai lung lant din graf
     * @return lantul gasit
     */
    public Path longestPath() {
        for (int i = 0; i < numberOfVertices; i++) {
            if (controller.findUser((long) i) != null) {
                boolean[] visited = new boolean[numberOfVertices];
                ArrayList<Integer> components = new ArrayList<>();

                int lastMax = maxLen;
                DFSUtil2(i, 0, visited, components);

                if (lastMax < maxLen) {
                    startNode = i;
                    theMostSociableCommunity = components;
                }
            }
        }

        return new Path(maxLen, startNode, endNode);
    }

    /**
     * parcurge DFS tot graful pentru a afla numarul de componente
     */
    public void DFS() {
        boolean[] visited = new boolean[numberOfVertices];

        for (int i = 0; i < numberOfVertices; i++) {
            if (controller.findUser((long) i) != null) {
                ArrayList<Integer> components = new ArrayList<>();

                if (!visited[i]) {
                    DFSUtil(i, visited, components);
                    allComponents.add(components);
                }
            }
        }
    }

    /**
     * Calculeaza cea mai sociabila comunitate
     * @return comunitatea gasita
     */
    public ArrayList<Integer> getTheMostSociableCommunity() {
        longestPath();
        return theMostSociableCommunity;
    }

    /**
     * @return numarul de componente conexe
     */
    public int connectedComponents() {
        DFS();
        return allComponents.size();
    }
}
