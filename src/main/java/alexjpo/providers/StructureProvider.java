package alexjpo.providers;

import alexjpo.model.Node;

public interface StructureProvider {
    String getSeparator();

    Node nodeByPath(String path);

    Node[] getRoots();

    Node[] getRootsByPath(String path);

}
