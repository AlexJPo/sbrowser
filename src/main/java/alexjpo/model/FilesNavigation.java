package alexjpo.model;

import alexjpo.providers.StructureProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.regex.Pattern;

@Component
public class FilesNavigation implements Breadcrumb {
    private ObservableList<String> breadcrumbModel;

    private String foldersPath;
    private String defaultFoldersPath;
    private String separator;
    private String crumbSeparator;

    private boolean isFileSystem = true;

    @Autowired
    private StructureProvider provider;

    @PostConstruct
    public void init() {
        foldersPath = "";
        separator = Pattern.quote(provider.getSeparator());
        breadcrumbModel = FXCollections.observableArrayList();
    }

    @Override
    public void setCrumb(String crumb) {
        foldersPath = crumb;

        if (breadcrumbModel.size() > 0)
            breadcrumbModel.clear();

        if (isFileSystem) {
            for (String item: foldersPath.split(separator)) {
                breadcrumbModel.add(item + crumbSeparator);
            }
        } else {
            String tempFoldersPath = foldersPath
                                    .replace(defaultFoldersPath, "")
                                    .trim();

            for (String item: tempFoldersPath.split(separator)) {
                breadcrumbModel.add(item + crumbSeparator);
            }
        }
    }

    @Override
    public void removeCrumb(int selectedItem) {
        if (breadcrumbModel.size() > 0)
            breadcrumbModel.remove(selectedItem + 1, breadcrumbModel.size());
    }

    @Override
    public void pathUpdate() {
        foldersPath = defaultFoldersPath;
        if (!foldersPath.trim().isEmpty() && isFileSystem == true) {
            foldersPath += File.separator;
        }

        for (int i = 0; i < breadcrumbModel.size(); i++) {
            foldersPath += breadcrumbModel.get(i).replace(crumbSeparator, "") + File.separator;
        }
    }

    @Override
    public String getPath() {
        return foldersPath;
    }

    @Override
    public boolean isFileSystem() {
        return isFileSystem;
    }

    public void setIsFileSystem(boolean project) {
        this.isFileSystem = project;
    }

    public void clearModel() {
        if (breadcrumbModel.size() > 0)
            breadcrumbModel.clear();
    }

    public ObservableList<String> getModel() {
        return breadcrumbModel;
    }

    public String getDefaultFoldersPath() {
        return defaultFoldersPath;
    }

    public void setDefaultFoldersPath(String defaultPath) {
        this.defaultFoldersPath = defaultPath;
    }

    public String getFoldersPath() {
        return foldersPath;
    }

    public void setFoldersPath(String rootName) {
        this.foldersPath = rootName;
    }

    public String getCrumbSeparator() {
        return crumbSeparator;
    }

    public void setCrumbSeparator(String crumbSeparator) {
        this.crumbSeparator = crumbSeparator;
    }
}
