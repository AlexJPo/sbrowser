package alexjpo.model;

import alexjpo.providers.StructureProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Component
public class ClassPathNavigation  {
    /*private ObservableList<String> breadcrumbModel;
    private String foldersPath;

    @Autowired
    private StructureProvider provider;

    private String separator;

    @PostConstruct
    public void init() {
        foldersPath = "";
        separator = Pattern.quote(provider.getSeparator());
        breadcrumbModel = FXCollections.observableArrayList();
    }

    @Override
    public void setCrumb(String path) {

    }

    @Override
    public void removeCrumb(int index) {

    }

    @Override
    public void pathUpdate() {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public boolean isFileSystem() {
        return false;
    }

    @Override
    public boolean isMainRoot() {
        return false;
    }

    public ObservableList<String> getModel() {
        return breadcrumbModel;
    }*/
}
