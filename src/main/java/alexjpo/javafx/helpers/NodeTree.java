package alexjpo.javafx.helpers;

import java.util.List;

public class NodeTree {
    String key;
    String[] value;

    NodeTree leftChild;
    NodeTree rightChild;

    NodeTree(String key, String[] value) {
        this.key = key;
        this.value = value;
    }
}
