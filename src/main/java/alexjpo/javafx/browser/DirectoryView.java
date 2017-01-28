package alexjpo.javafx.browser;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;

@FXMLView("/fxml/directory.fxml")
public class DirectoryView extends AbstractFxmlView {

    public DirectoryView() {
        setTitle("Mega Structure Browser");
    }

}
