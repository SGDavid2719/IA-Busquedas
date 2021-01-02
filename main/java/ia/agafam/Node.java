package ia.agafam;

import java.util.ArrayList;
import java.util.Collections;

public class
Node implements Comparable<Node> {
    int x;
    int y;

    Node nodePrevi;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ArrayList<Node> veinats() {
        ArrayList<Node> veinats = new ArrayList<Node>();

        for (Direccio d : Direccio.values()) {
            if (d != Direccio.C) // ignora la meva posició
            {
                Punt p = d.coordenadesVeinat(new Punt(x, y, 0, 0));
                veinats.add(new Node(p.x, p.y));
            }
        }
        Collections.shuffle(veinats);
        return veinats;
    }

    @Override
    public boolean equals(Object node) // Per comparar dos nodes, mira les coordenades x i y
    {
        if (node == null) return false;
        if (((Node) node).x == this.x && ((Node) node).y == this.y) return true;
        else return false;
    }

    public Node getNodePrevi() {
        return nodePrevi;
    }

    public void setNodePrevi(Node nodePrevi) {
        this.nodePrevi = nodePrevi;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int compareTo(Node node2) {
        // retorna -1, 0, o 1 depenent de si és el node1 és menor que el node2, igual o major

        return 0;
    }
}