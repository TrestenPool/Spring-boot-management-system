package mvc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mvc.controllers.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppMain extends Application {
    private static final Logger logger = LogManager.getLogger();

    /** Main **/
    public static void main(String[] args) {
        launch(args);
    }

    /** init **/
    @Override
    public void init() throws Exception {
        super.init();
    }

    /** start **/
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/personViews/main_view.fxml"));
        loader.setController(MainController.getInstance());
        Parent rootNode = loader.load();
        stage.setScene(new Scene(rootNode));

        stage.setTitle("CS 4743 Fall 2021");
        stage.show();
    }

}
