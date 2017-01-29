package alexjpo.javafx.helpers;

import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import alexjpo.providers.StructureProvider;

import javax.annotation.PostConstruct;

@Component
public class BreadCrumb {
	private ObservableList<String> modelBreadCrumbList = FXCollections.observableArrayList();
	private String folderPath ="";

	@Autowired
	private StructureProvider provider;

	private String separator;

	@PostConstruct
	public void init() {
		separator = Pattern.quote(provider.getSeparator());
	}

	/**
	 * Set data for 'modelBreadCrumbList'
	 * @param folderPath
	 */
	public void setCrumb(String folderPath) {
		if (modelBreadCrumbList.size() > 0) { modelBreadCrumbList.clear(); }
				
		this.folderPath = folderPath;
		
		for (String item: folderPath.split(separator)) {
			this.modelBreadCrumbList.add(item + "   >");
		}
	}
	
	/**
	 * Return javafx.helpers 'modelBreadCrumbList'
	 * @return modelBreadCrumbList
	 */
	public ObservableList<String> getCrumb() {
		return this.modelBreadCrumbList;
	}
	
	/**
	 * Update 'modelBreadCrumbList'
	 * @param selectedIndex
	 */
	public void updateCrumb(int selectedIndex) {
		if (this.modelBreadCrumbList.size() > 0) {
			this.modelBreadCrumbList.remove(selectedIndex+1, modelBreadCrumbList.size());
			updatePathFolder();
		}
	}
	
	/**
	 * Update 'folderPath' after backward to parent directory
	 */
	private void updatePathFolder() {
		this.folderPath = "";
		
		for (int i = 0; i < modelBreadCrumbList.size(); i++) {
			this.folderPath += modelBreadCrumbList.get(i).replace("   >", separator).trim();
		}
	}
	
	/**
	 * Return bread crumb folder path for current directory
	 * @return String 'folderPath'
	 */
	public String getBreadCrumbPath() {
		return this.folderPath;
	}
}
