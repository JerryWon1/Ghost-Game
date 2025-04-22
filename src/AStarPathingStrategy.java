import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    /**
     * Return a list containing a single point representing the next step toward a goal
     * If the start is within reach of the goal, the returned list is empty.
     *
     * @param start the point to begin the search from
     * @param end the point to search for a point within reach of
     * @param canPassThrough a function that returns true if the given point is traversable
     * @param withinReach a function that returns true if both points are within reach of each other
     * @param potentialNeighbors a function that returns the neighbors of a given point, as a stream
     */
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {
        Node startNode = new Node(start.x, start.y, 0, end, null);
        PriorityQueue<Node> openList = new PriorityQueue<>();
        openList.add(startNode);
        List<Node> closedList = new ArrayList<>();

        while (!openList.isEmpty()) {
            Node currentNode = openList.remove();
            closedList.add(currentNode);
            if (withinReach.test(currentNode, end)) {
                List<Point> path = new ArrayList<>();
                Node current = currentNode;
                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                return path;
            }
            Stream<Node> neighbors = potentialNeighbors.apply(currentNode)
                    .filter(canPassThrough)
                    .map(neighbor -> new Node(neighbor.x, neighbor.y, currentNode.getG(), end, currentNode));
            for (Node neighbor : neighbors.toList()) {
                int tentativeG = currentNode.getG() + currentNode.manhattanDistanceTo(neighbor);
                if (closedList.contains(neighbor)) {
                    continue;
                }
                if (!openList.contains(neighbor)) {
                    neighbor.setG(tentativeG);
                    openList.add(neighbor);
                } else if (tentativeG < neighbor.getG()) {
                    neighbor.setG(tentativeG);
                    openList.add(neighbor);
                }
            }
        }

        // No valid positions
        return new ArrayList<>(); // Return an empty list if no path is found
    }
}
