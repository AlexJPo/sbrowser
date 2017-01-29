package alexjpo.model;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.Set;

public class ClassPathNode implements Node {
    private String projectPackage;

    public ClassPathNode(String file) throws IOException {
        this.projectPackage = file;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Node[] getChildren() {
        Set<ClassPath.ClassInfo> classInfo = null;
        try {
            classInfo = ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(projectPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node[] nodes = classInfo.stream()
                .map(file -> {
                    try {
                        return new ClassPathNode(file.getSimpleName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .distinct()
                .toArray(Node[]::new);

        return nodes;
    }

    @Override
    public String getName() { return projectPackage; }

    @Override
    public String getPath() { return projectPackage; }
}
