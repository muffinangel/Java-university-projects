package xo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayingScene.fxml"));
        Parent root = loader.load();
        GameStageController controller = loader.getController();
        primaryStage.setTitle("Tic Tac Toe Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnHidden(e -> controller.shutdown());
        primaryStage.setOnCloseRequest(event -> Platform.exit()); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}
