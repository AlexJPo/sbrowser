package alexjpo.javafx.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private T root;
    private Tree<T> parent;
    private List<Tree<T>> children;

    public Tree() {
        this.root = null;
        this.parent = null;
        this.children = new ArrayList<Tree<T>>();
    }
    public Tree(T data) {
        this.root = data;
        this.parent = null;
        this.children = new ArrayList<Tree<T>>();
    }

    public void addChildNode(Tree<T> child) {
        child.parent = this;

        if (!children.contains(child))
            children.add(child);
    }

    public T getRoot() {
        return root;
    }

    public Tree<T> findNode(T key, Tree<T> node) {
        if (node.children != null) {
            if (node.root.equals(key)) {
                return node;
            } else {
                for (Tree<T> childreNode: node.children) {
                    Tree<T> searchedNode = findNode(key, childreNode);

                    if (searchedNode != null)
                        return searchedNode;
                }
            }
        }
        return null;
    }

    public List<String> getChildAsList() {
        List<String> child = new ArrayList<String>();

        for(Tree<T> item: children) {
            File shorName = new File(item.root.toString());

            child.add(shorName.getName());
        }

        return child;
    }
}
