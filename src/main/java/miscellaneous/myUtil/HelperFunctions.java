package miscellaneous.myUtil;

import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelperFunctions {
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    public static void errorPopUp(String title, String headerText, String contentText){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

    public static void warningPopUp(String title, String headerText, String contentText){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

//    public static <T extends Initializable> BorderPane loadNewViewWithController(BorderPane rootPane, String fxmlFile, T login_controller){
//        FXMLLoader loader = new FXMLLoader(HelperFunctions.class.getResource(fxmlFile));
//        loader.setController(login_controller);
//        Parent rootNode = null;
//        // places the login screen in the middle of the border pane
//        try {
//            rootNode = loader.load();
//            rootPane.setCenter(rootNode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return rootPane;
//    }

}
