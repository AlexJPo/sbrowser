package alexjpo.javafx.helpers;

import java.io.File;

import alexjpo.model.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Folders {
	private ObservableList<String> modelFolderList = FXCollections.observableArrayList();
	
	public Folders() {
		
	}

	/**
	 * Set 'modelFolderList' data
	 */
	public void setFoldersModel(Node[] nodes) {
		modelFolderList.clear();
		
		for (Node node: nodes) {
			if (!node.isLeaf()) {
				modelFolderList.add(node.getName());
			}
		}
	}
	
	/**
	 * Return javafx.helpers 'modelFolderList'
	 * @return ObservableList<String> modelFolderList
	 */
	public ObservableList<String> getFoldersModel() {
		return this.modelFolderList;
	}
}
