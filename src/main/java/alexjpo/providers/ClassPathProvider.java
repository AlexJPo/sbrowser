package alexjpo.providers;

import alexjpo.model.FileSystemNode;
import alexjpo.model.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ClassPathProvider extends FileSystemProvider {

    @Override
    public Node[] getRoots() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(".");

        Node root  = new FileSystemNode(new File(url.getFile()));
        return new Node[] {root};
    }
}
