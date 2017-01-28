package alexjpo.javafx.browser;

import alexjpo.javafx.helpers.BreadCrumb;
import alexjpo.javafx.helpers.FolderStatus;
import alexjpo.javafx.helpers.Folders;
import alexjpo.model.Node;
import alexjpo.providers.StructureProvider;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@FXMLController
public class DirectoryController {
	Logger log = LoggerFactory.getLogger(getClass());
		
	private String folderPath;
	private String selectedItem;
	
	private FolderStatus nodeStatus;

	@Autowired
	private BreadCrumb breadcrumb;

	private Folders nodesModel;
	
	private Node node;
	private Node[] children;

	@Autowired
	private StructureProvider structureProvider;

	@FXML
	private ListView<String> breadCrumbList;
	@FXML
	private ListView<String> diskList;
	@FXML
	private ListView<String> nodeList;
	@FXML
	private Label statusFolderLabel;

	@FXML
	private void initialize() {
		nodesModel = new Folders();
		nodeStatus = new FolderStatus();
		setDriversList();		
	}

	/**
	 * Add roots system to 'diskList'
	 */
	private void setDriversList() {
		log.info("setDriversList is invoked");
		Node[] roots = structureProvider.getRoots();
		ObservableList<String> rootsModel = FXCollections.observableArrayList();
		for (Node root: roots) {
			log.info("ROOT: {}", root.getName());
			rootsModel.add(root.getName());
		}
		diskList.setItems(rootsModel);
	}
	
	/**
	 * MouseEvent for ListView 'diskList'. Set data 'nodeList'
	 */
	public void setDiskFolders(MouseEvent event) {
		ListView<String> result = (ListView<String>) event.getSource();		
		selectedItem = result.getSelectionModel().getSelectedItem();
		
		if (selectedItem != null) {			
			folderPath = selectedItem + structureProvider.getSeparator();
			
			node = structureProvider.nodeByPath(selectedItem);
			children = node.getChildren();
					
			nodeStatus.count(children);
			statusFolderLabel.setText(nodeStatus.getStatus());
				
			setChildren(children);
				
			breadcrumb.setCrumb(node.getPath());
			breadCrumbList.setItems(breadcrumb.getCrumb());
		}
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
				folderPath = breadcrumb.getBreadCrumbPath() +
						structureProvider.getSeparator() + selectedItem + structureProvider.getSeparator();
				
				node = structureProvider.nodeByPath(folderPath);
				children = node.getChildren();
				
				if (children != null) {
					nodeStatus.count(children);
					statusFolderLabel.setText(nodeStatus.getStatus());
					
					breadcrumb.setCrumb(node.getPath());
					breadCrumbList.setItems(breadcrumb.getCrumb());
					
					setChildren(children);
				}
			}
		}		
	}
	
	/**
	 * Exit children node by click on bread crumb item
	 */
	public void exitFolder(MouseEvent event) {
		if (event.getClickCount() == 2) {
			ListView<String> result = (ListView<String>) event.getSource();
			int selectedCrumb = result.getSelectionModel().getSelectedIndex();
			
			if (selectedCrumb != -1) {
				breadcrumb.updateCrumb(selectedCrumb);
				breadCrumbList.setItems(breadcrumb.getCrumb());
				
				node = structureProvider.nodeByPath(breadcrumb.getBreadCrumbPath());
				children = node.getChildren();
				
				nodeStatus.count(children);
				statusFolderLabel.setText(nodeStatus.getStatus());
				
				setChildren(children);
			}			
		}
	}
}
