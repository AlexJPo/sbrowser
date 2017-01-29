package alexjpo.providers;

import alexjpo.model.ClassPathNode;
import alexjpo.model.FileSystemNode;
import alexjpo.model.Node;
import com.google.common.reflect.ClassPath;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathProvider implements StructureProvider {

    @Override
    public String getSeparator() { return "/"; }

    @Override
    public Node nodeByPath(String path) {
        try {
            return new ClassPathNode(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Node[] getRoots() {
        ClassLoader classLoader = getClass().getClassLoader();
        Node[] nodes = null;

        try {
            Set<ClassPath.ClassInfo> classInfos = ClassPath.from(classLoader).getTopLevelClassesRecursive("alexjpo");
            List<String> packageNames = classInfos.stream().map(classInfo -> classInfo.getPackageName()).distinct().collect(Collectors.toList());

            nodes = packageNames.stream()
                    .map(file -> {
                        try {
                            return new ClassPathNode(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .toArray(Node[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nodes;
    }
}
