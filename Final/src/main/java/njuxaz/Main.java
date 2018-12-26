package njuxaz;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;




public class Main extends Application {
    public static Stage currentStage;
    public void start(Stage stage) throws Exception {
        currentStage=stage;

		

        Parent root = FXMLLoader.load(this.getClass().getResource("/Login.fxml"));
        
		Scene scene = new Scene(root, 600, 370);
        stage.setResizable(false);
        stage.setTitle("葫芦娃大战妖精");
        stage.getIcons().add(new Image("/2.png"));
        stage.setScene(scene);
        stage.show();

    }

    public void battleStage(){

    }
    public void gameOverStage(){

    }
    public static void main(String[] args) {
        launch(args);
    }
}
