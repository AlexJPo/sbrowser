package alexjpo.javafx.application;
	
import alexjpo.javafx.browser.DirectoryView;
import alexjpo.providers.ClassPathProvider;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import alexjpo.providers.FileSystemProvider;
import alexjpo.providers.StructureProvider;

@Lazy
@SpringBootApplication(scanBasePackages = "alexjpo")
public class JFXStarter extends AbstractJavaFxApplicationSupport {

//	@Override
//	public void start(Stage primaryStage) {
//		try {
//			FXMLLoader fxmlLoader = new FXMLLoader();
//			fxmlLoader.setLocation(getClass().getResource("/fxml/directory.fxml"));
//			Parent mainWindow = fxmlLoader.load();
//
//			Scene scene = new Scene(mainWindow, 400, 400);
//			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
//
//			primaryStage.setMinWidth(400);
//			primaryStage.setMinHeight(300);
//			primaryStage.setTitle("File explorer");
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Bean
	public StructureProvider structureProvider() {
		return new FileSystemProvider();
	}
	
	public static void main(String[] args) {
		launchApp(JFXStarter.class, DirectoryView.class, args);
	}
}
