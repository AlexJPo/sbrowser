package alexjpo.javafx.browser;

import alexjpo.javafx.helpers.Tree;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;

import java.util.List;


@FXMLController
public class DirectoryController {
	Logger log = LoggerFactory.getLogger(getClass());

	enum StructureType {
		FileSystem, JavaProject, Json
	}

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

	private Tree tree;
	private StructureType structureType;

	private ObservableList<String> rootsModel;

	@FXML
	private void initialize() {
		nodesModel = new Folders();
		nodeStatus = new FolderStatus();
		rootsModel = FXCollections.observableArrayList();

		statusFolderLabel.setText(nodeStatus.getStatus());
		structureType = StructureType.FileSystem;

		setBrowserData("", "   >");
	}

	private void setDefaultModel(Node[] roots) {
		ObservableList<String> rootsModel = FXCollections.observableArrayList();

		for (Node root: roots) {
			rootsModel.add(root.getName());
		}
		nodeList.setItems(rootsModel);
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

		if (javaProject != null)
			findDirectory(javaProject);
	}
	private void findDirectory(File dir) {
		File[] dirFiles = dir.listFiles();

		javaProjectIsFind = false;

		for (File file: dirFiles) {
			if (file.isDirectory() && file.getName().equals("src")) {
				breadcrumb.setFoldersPath(file.getAbsolutePath() + File.separator);
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
			try {
				String readedJsonFile = readJson(jsonFile.getAbsolutePath());
				parseJson(readedJsonFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			structureType = StructureType.Json;
			setBrowserData("", structureProvider.getSeparator());
		}
	}
	private String readJson(String patch) throws IOException {
		InputStream is = new FileInputStream(patch);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));

		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();

		while(line != null){
			if (line.indexOf("п»ї") != -1) {
				line = line.replace("п»ї", "").trim();
			}

			sb.append(line.trim());
			line = buf.readLine();
		}

		return sb.toString();
	}
	private void parseJson(String json) {
		tree = new Tree("");

		try {
			JSONObject jsonOject = new JSONObject(json.trim());
			String[] keys = JSONObject.getNames(jsonOject);

			fillJsonTree(tree, jsonOject, keys, "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void fillJsonTree(Tree tree, JSONObject jsonObject, String[] keys, String pathRoot) {
		for (String key : keys)
		{
			pathRoot = tree.getRoot() + key + structureProvider.getSeparator();

			Object jsonKey = jsonObject.get(key);

			if (jsonKey instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) jsonKey;

				Tree child = new Tree(pathRoot);
				tree.addChildNode(child);

				for (int i = 0; i < jsonArray.length(); i++) {
					if (jsonArray.get(i).getClass().getName().equals("java.lang.String")) {

						Tree childArr = new Tree(pathRoot + jsonArray.get(i) + structureProvider.getSeparator());
						child.addChildNode(childArr);
					} else {
						JSONObject arrayElement = (JSONObject) jsonArray.get(i);
						String[] arrKeys = arrayElement.getNames(arrayElement);

						Tree childArr = new Tree(pathRoot + i + structureProvider.getSeparator());
						child.addChildNode(childArr);

						if (arrKeys.length > 0) {
							fillJsonTree(childArr, arrayElement, arrKeys, pathRoot);
						}
					}
				}
			} else if (jsonKey instanceof JSONObject) {
				JSONObject jsonElement = (JSONObject) jsonKey;
				String[] elementKeys = jsonElement.getNames(jsonElement);

				Tree child = new Tree(pathRoot);
				tree.addChildNode(child);

				if (elementKeys.length > 0) {
					fillJsonTree(child, jsonElement, elementKeys, pathRoot);
				}
			} else {
				Tree child = new Tree(pathRoot);
				child.addChildNode(new Tree(jsonKey));

				tree.addChildNode(child);
			}
		}
	}

	@FXML
	private void showFileSystem() {
		breadcrumb.setIsFileSystem(true);
		breadcrumb.removeCrumb(0);
		breadcrumb.pathUpdate();
		breadcrumbList.setItems(breadcrumb.getModel());

		setBrowserData("", "   >");
	}

	private void setBrowserData(String folderPath, String crumbSeparator) {
		if (rootsModel.size() > 0) {
			rootsModel.clear();
		}

		breadcrumb.clearModel();
		breadcrumb.setCrumbSeparator(crumbSeparator);
		breadcrumb.setFoldersPath(folderPath);
		breadcrumb.setDefaultFoldersPath(folderPath);

		if (structureType != StructureType.Json) {
			Node[] roots = structureProvider.getRootsByPath(breadcrumb.getFoldersPath());

			for (Node root: roots) {
				rootsModel.add(root.getName());
			}
			nodeList.setItems(rootsModel);
			breadcrumbList.setItems(breadcrumb.getModel());

			tree = new Tree(breadcrumb.getFoldersPath());
		} else {
			Tree findRoot = tree.findNode(breadcrumb.getFoldersPath(), tree);

			if (rootsModel.size() > 0) {
				rootsModel.clear();
			}

			List<String> temp = findRoot.getChildAsList();
			temp.forEach(item -> rootsModel.add(item));
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
				switch (structureType) {
					case FileSystem:
					case JavaProject:
						if (breadcrumb.getPath().equals(""))
						{
							folderPath = selectedItem;
						} else {
							folderPath = breadcrumb.getPath()
									+ selectedItem
									+ structureProvider.getSeparator();
						}

						File selectedDir = new File(folderPath);
						File[] selectedDirFiles = selectedDir.listFiles();

						if (selectedDir.isDirectory() && selectedDirFiles != null) {
							Tree findRoot = tree.findNode(selectedDir.getAbsolutePath(), tree);

							if (findRoot == null) {
								findRoot = new Tree(selectedDir.getAbsolutePath());
								tree.addChildNode(findRoot);
							}

							for (File item: selectedDirFiles) {
								Tree child = new Tree(item.getAbsolutePath());
								findRoot.addChildNode(child);
							}

							breadcrumb.setCrumb(folderPath);
							breadcrumbList.setItems(breadcrumb.getModel());

							if (rootsModel.size() > 0) {
								rootsModel.clear();
							}

							List<String> temp = findRoot.getChildAsList();
							temp.forEach(item -> rootsModel.add(item));
						}
						break;
					case Json:
						if (breadcrumb.getPath().equals(""))
						{
							folderPath = selectedItem + structureProvider.getSeparator();
						} else {
							folderPath = breadcrumb.getPath()
									+ selectedItem
									+ structureProvider.getSeparator();
						}

						Tree findRoot = tree.findNode(folderPath, tree);

						if (findRoot != null) {
							List<String> temp = findRoot.getChildAsList();

							if (temp.size() > 0) {
								breadcrumb.setCrumb(folderPath);
								breadcrumbList.setItems(breadcrumb.getModel());

								if (rootsModel.size() > 0) {
									rootsModel.clear();
								}

								temp.forEach(item -> rootsModel.add(item));
							}
						}
						break;
				}

				/*folderPath = breadcrumb.getPath() +
						structureProvider.getSeparator() +
						selectedItem +
						structureProvider.getSeparator();*/

				/*node = structureProvider.nodeByPath(folderPath);
				children = node.getChildren();

				if (children != null) {
					nodeStatus.count(children);
					statusFolderLabel.setText(nodeStatus.getStatus());
					
					breadcrumb.setCrumb(node.getPath());
					breadcrumbList.setItems(breadcrumb.getModel());
					
					setChildren(children);
				}*/
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

			Tree findRoot = null;

			if (structureType != StructureType.Json) {
				File path = new File(breadcrumb.getPath());
				findRoot = tree.findNode(path.getAbsolutePath(), tree);
			} else {
				findRoot = tree.findNode(breadcrumb.getPath(), tree);
			}

			if (rootsModel.size() > 0) {
				rootsModel.clear();
			}

			List<String> temp = findRoot.getChildAsList();
			temp.forEach(item -> rootsModel.add(item));
		}

		/*if (selectedCrumb != -1) {
			breadcrumb.removeCrumb(selectedCrumb);
			breadcrumb.pathUpdate();

			breadcrumbList.setItems(breadcrumb.getModel());

			node = structureProvider.nodeByPath(breadcrumb.getPath());
			children = node.getChildren();

			nodeStatus.count(children);
			statusFolderLabel.setText(nodeStatus.getStatus());

			setChildren(children);
		}*/
	}
}