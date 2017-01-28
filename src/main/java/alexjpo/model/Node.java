package alexjpo.model;

public interface Node {
    boolean isLeaf();

    Node[] getChildren();

    String getName();

    String getPath();
}
