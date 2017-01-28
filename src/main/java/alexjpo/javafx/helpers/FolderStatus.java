package alexjpo.javafx.helpers;

import alexjpo.model.Node;

public class FolderStatus {
	private int foldersCount, filesCount;
	
	public FolderStatus() {
		this.foldersCount = 0;
		this.filesCount = 0;
	}
	
	/**
	 * count files and folder in current directory
	 * @param files
	 */
	public void count(Node[] files) {
		foldersCount = 0;
		filesCount = 0;
		
		for (Node file: files) {
			if (file.isLeaf()) {
				filesCount++;
			} else {
				foldersCount++;
			}
		}
	}

	/**
	 * Get 'status' text
	 * @return String 'status' 
	 */
	public String getStatus() {
		return "Total folders: " + foldersCount + ", Total files: " + filesCount;
	}
}
