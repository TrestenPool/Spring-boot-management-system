package mvc.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import mvc.gateway.PersonGatewaySpringBoot;
import mvc.gateway.Session;
import mvc.models.AuditTrailRecord;
import mvc.models.PagingResults;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    /** Variables **/
    public static final Logger LOGGER = LogManager.getLogger(); // logger
    private static MainController instance = null; // our singleton var
    private Session session; // the session
    private PersonGatewaySpringBoot personGateway; // the reference to the person gateway

    // fxml object we will connect our other views to
    @FXML
    private BorderPane rootPane;

    /** Constructor **/
    private MainController() {
       personGateway = new PersonGatewaySpringBoot();
    }

    /** Switch View **/
    public void switchView(ScreenType screenType, Object... args){
        // each of the cases will assign a different variable to these two
        String viewFileName = "";
        MyController controller = null;

        switch (screenType){
            /** Person Login **/
            case LOGIN:
                viewFileName = "/personViews/person_login.fxml";
                controller = new Login_Controller();
                break;
            /** Person List **/
            case PERSON_LIST:
                viewFileName = "/personViews/person_list.fxml";
                controller = new List_Controller();
                break;
            /** Person Detail **/
            case PERSON_DETAIL:
                viewFileName = "/personViews/person_detail.fxml";
                // no argument passsed, adding a person
                if(args.length == 0){
                    controller = new Detail_Controller();
                }
                // Person object was passed in
                else if (args[0] instanceof Person){
                    controller = new Detail_Controller((Person) args[0]);
                }
                // argument was passed in but it was not a person
                else{
                    LOGGER.info("You did not pass in a person to the person_detail view...");
                    return;
                }
                break;
            /** Audit trail **/
            case AUDIT_LIST:
                Person personPassed = (Person) args[0];
                String personID = String.valueOf(personPassed.getIdentifcationNumber());
                viewFileName = "/personViews/audit_list.fxml";
                ArrayList<AuditTrailRecord> auditTrailList = personGateway.fetchAuditTrails(session.getSessionID(), personID );
                controller = new AuditTrailList_Controller(auditTrailList, personPassed);
                break;

        }//end of switch

        // switch to the specified view in the switch
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(viewFileName));
        loader.setController(controller);
        Parent rootNode = null;
        try {
            rootNode = loader.load();
            rootPane.setCenter(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end of switchView method

    /** Initialize **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // start of the with the login view
        switchView(ScreenType.LOGIN);
    }

    /** Get instance of **/
    public static MainController getInstance(){
        if(instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    /*** Getters and Setters **/
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public PersonGatewaySpringBoot getPersonGateway() {
        return personGateway;
    }
    public void setPersonGateway(PersonGatewaySpringBoot personGateway) {
        this.personGateway = personGateway;
    }

}