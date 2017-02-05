package alexjpo.javafx.browser;

import alexjpo.javafx.helpers.FolderStatus;
import alexjpo.javafx.helpers.Folders;
import alexjpo.model.FilesNavigation;
import alexjpo.model.Node;
import alexjpo.providers.StructureProvider;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;


@FXMLController
public class DirectoryController {
	Logger log = LoggerFactory.getLogger(getClass());
		
	private String folderPath;
	private String selectedItem;
	private boolean javaProjectIsFind;
	
	private FolderStatus nodeStatus;

	private FileChooser fileChooser;
	private DirectoryChooser directoryChooser;

	@Autowired
	private FilesNavigation breadcrumb;

	private Folders nodesModel;
	
	private Node node;
	private Node[] children;

	@Autowired
	private StructureProvider structureProvider;

	@FXML
	private ListView<String> breadcrumbList;
	@FXML
	private ListView<String> nodeList;
	@FXML
	private Label statusFolderLabel;
	@FXML
	private Button javaProjectDialog;
	@FXML
	private Button jsonFileDialog;
	@FXML
	private Button fileSystemShow;
	@FXML
	private Button homeBtn;


	@FXML
	private void initialize() {
		nodesModel = new Folders();
		nodeStatus = new FolderStatus();

		statusFolderLabel.setText(nodeStatus.getStatus());

		setBrowserData("", "   >");
	}

	@FXML
	private void homeRoot() {
		setBrowserData(breadcrumb.getDefaultFoldersPath(), breadcrumb.getCrumbSeparator());
	}

	@FXML
	private void openJavaProjectDialog() {
		directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select java project");

		File javaProject = directoryChooser.showDialog(new Stage());

		if (javaProject != null) {
			findDirectory(javaProject);
		}
	}
	private void findDirectory(File dir) {
		File[] dirFiles = dir.listFiles();

		javaProjectIsFind = false;

		for (File file: dirFiles) {
			if (file.isDirectory() && file.getName().equals("src")) {
				breadcrumb.setFoldersPath(file.getAbsolutePath());
				javaProjectIsFind = true;
				break;
			}
		}

		if (javaProjectIsFind) {
			breadcrumb.setIsFileSystem(false);
			setBrowserData(breadcrumb.getFoldersPath(), "  .");
		}
	}

	@FXML
	private void openJsonFileDialog() {
		fileChooser = new FileChooser();
		fileChooser.setTitle("Select json file");
		fileChooser.getExtensionFilters().add(
			new FileChooser.ExtensionFilter("JSON", "*.json")
		);

		File jsonFile = fileChooser.showOpenDialog(new Stage());

		if (jsonFile != null) {

		}
	}

	@FXML
	private void showFileSystem() {
		setBrowserData("", "   >");

		breadcrumb.setIsFileSystem(true);
		breadcrumb.removeCrumb(0);
		breadcrumb.pathUpdate();
		breadcrumbList.setItems(breadcrumb.getModel());
	}

	private void setBrowserData(String folderPath, String crumbSeparator) {
		breadcrumb.clearModel();
		breadcrumb.setCrumbSeparator(crumbSeparator);

		breadcrumb.setFoldersPath(folderPath);
		breadcrumb.setDefaultFoldersPath(folderPath);

		breadcrumbList.setItems(breadcrumb.getModel());

		Node[] roots = structureProvider.getRootsByPath(breadcrumb.getFoldersPath());

		ObservableList<String> rootsModel = FXCollections.observableArrayList();

		for (Node root: roots) {
			rootsModel.add(root.getName());
		}
		nodeList.setItems(rootsModel);
	}

	/**
	 * Add children to 'nodeList'
	 * @param nodes - list of disk children
	 */
	private void setChildren(Node[] nodes) {
		nodesModel.setFoldersModel(nodes);
		nodeList.setItems(nodesModel.getFoldersModel());
	}
	
	/**
	 * Open children node in current parent directory
	 */
	public void enterFolder(MouseEvent event) {
		if (event.getClickCount() == 2) {
			ListView<String> result = (ListView<String>) event.getSource();
			selectedItem = result.getSelectionModel().getSelectedItem();
			
			if (selectedItem != null) {
				folderPath = breadcrumb.getPath() +
						structureProvider.getSeparator() +
						selectedItem +
						structureProvider.getSeparator();

				node = structureProvider.nodeByPath(folderPath);
				children = node.getChildren();

				if (children != null) {
					nodeStatus.count(children);
					statusFolderLabel.setText(nodeStatus.getStatus());
					
					breadcrumb.setCrumb(node.getPath());
					breadcrumbList.setItems(breadcrumb.getModel());
					
					setChildren(children);
				}
			}
		}		
	}
	
	/**
	 * Exit children node by click on bread crumb item
	 */
	public void exitFolder(MouseEvent event) {
		ListView<String> result = (ListView<String>) event.getSource();
		int selectedCrumb = result.getSelectionModel().getSelectedIndex();

		if (selectedCrumb != -1) {
			breadcrumb.removeCrumb(selectedCrumb);
			breadcrumb.pathUpdate();

			breadcrumbList.setItems(breadcrumb.getModel());

			node = structureProvider.nodeByPath(breadcrumb.getPath());
			children = node.getChildren();

			nodeStatus.count(children);
			statusFolderLabel.setText(nodeStatus.getStatus());

			setChildren(children);
		}
	}
}