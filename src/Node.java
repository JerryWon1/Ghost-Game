public class Node extends Point implements Comparable<Node> {
    private int f;
    private int g;
    private int h;
    private Node parent;

    public Node(int x, int y, int g, Point destination, Node parent) {
        super(x, y);
        this.g = g;
        h = manhattanDistanceTo(destination);
        f = this.g + h;
        this.parent = parent;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.f, o.f);
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setG(int g) {
        this.g = g;
        f = g + h;
    }

    public int getG() {
        return g;
    }
}
