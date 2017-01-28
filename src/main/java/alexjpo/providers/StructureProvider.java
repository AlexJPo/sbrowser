package alexjpo.providers;

import alexjpo.model.Node;
import javafx.collections.ObservableList;

public interface StructureProvider {
    String getSeparator();

    Node nodeByPath(String path);

    Node[] getRoots();
}
