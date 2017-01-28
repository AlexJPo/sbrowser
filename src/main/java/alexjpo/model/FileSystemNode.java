package alexjpo.model;

import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;

public class FileSystemNode implements Node{
    private File file;

    public FileSystemNode(File file) {
        this.file = file;
    }

    public boolean isLeaf() {
        return !file.isDirectory();
    }

    public Node[] getChildren() {
        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            return new Node[0];
        }
        Node[] nodes = Arrays.stream(files)
                .map(file -> new FileSystemNode(file))
                .toArray(Node[]::new);
        return nodes;
    }

    @Override
    public String getName() {
        String name = file.getName();
        if (StringUtils.isEmpty(name)) {
            name = file.getPath();
        }
        return name;
    }

    @Override
    public String getPath() {
        return file.getPath();
    }
}
