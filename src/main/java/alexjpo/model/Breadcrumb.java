package alexjpo.model;

public interface Breadcrumb {
    void setCrumb(String path);

    void removeCrumb(int index);

    void pathUpdate();

    String getPath();

    boolean isFileSystem();
}
