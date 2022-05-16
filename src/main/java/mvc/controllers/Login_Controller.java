package mvc.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mvc.exceptions.UnauthorizedException;
import mvc.gateway.wirmockAPI.LoginGateway;
import mvc.gateway.Session;
import mvc.gateway.wirmockAPI.LoginGatewaySpringBoot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import miscellaneous.pw_hash.HashUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Login_Controller implements Initializable, MyController {
    public static final Logger LOGGER = LogManager.getLogger(); // logger

    /** FXML Variables **/
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    /** Constructor **/
    public Login_Controller() {
    }

    /** initializable **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize to test data
        username.setText("John");
        password.setText("easy");
    }

    /** Login Button clicked **/
    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = this.username.getText();
        String hashedPassword = HashUtils.getCryptoHash(this.password.getText(), "SHA-256");

        Session session = null;
        try{
            // connection to wiremock
//            session = LoginGateway.login(username, hashedPassword);

            // connection to spring boot, session was automatically submitted to the session table
            session = LoginGatewaySpringBoot.login(username, hashedPassword);
        }
        catch(UnauthorizedException u){
            // shows error if the username or password is incorrect
           miscellaneous.myUtil.HelperFunctions.errorPopUp("Login error", "Failed login", "Username or password failed");
           return;
        }

        // sets the session in the main controller
        MainController.getInstance().setSession(session);
        MainController.getInstance().switchView(ScreenType.PERSON_LIST);

    }//end of loginButtonClicked
}//end of LoginController