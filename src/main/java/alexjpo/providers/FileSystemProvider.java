package alexjpo.providers;

import alexjpo.model.FileSystemNode;
import alexjpo.model.Node;

import java.io.File;
import java.util.Arrays;

public class FileSystemProvider implements StructureProvider {

    public String getSeparator() {
        return File.separator;
    }

    public Node nodeByPath(String path) {
        return new FileSystemNode(new File(path));
    }

    @Override
    public Node[] getRoots() {
        File[] files = File.listRoots();
        Node[] nodes = Arrays.stream(files)
                .map(file -> new FileSystemNode(file))
                .toArray(Node[]::new);

        return nodes;
    }
}
