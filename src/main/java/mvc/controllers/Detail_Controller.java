package mvc.controllers;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mvc.gateway.PersonGatewaySpringBoot;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static miscellaneous.myUtil.HelperFunctions.*;

public class Detail_Controller implements Initializable,MyController{
    /** Local Variables **/
    public static final Logger LOGGER = LogManager.getLogger(); // logger
    private boolean isUpdatingPerson;// set depending on which constructor is called in instancinating this class
    private Person detailPerson;// our reference to the Person we will be updating or creating
    private PersonGatewaySpringBoot personGateway; // the reference to the person gateway

    /** FXML element references **/
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField id;
    @FXML
    private TextField age;
    @FXML
    private DatePicker dob;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab personDetailTab;
    @FXML
    private Tab auditTrailTab;

    /** Constructors **/
    // Creating a new person
    public Detail_Controller() {
        isUpdatingPerson = false; // sets to true if we are updating a person
        personGateway = new PersonGatewaySpringBoot(); // reference to the personGateway to call api
    }

    // Updating an existing person
    public Detail_Controller(Person person) {
        isUpdatingPerson = true; // Set to true because we will be updating the person
        detailPerson = person; // reference is pointing to the person that was passed in
        personGateway = new PersonGatewaySpringBoot();
    }

    /** Show audit trail list view **/
    @FXML
    void showAuditTrail(MouseEvent event) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();

        // no tab selected
        if(tab == null){
            System.out.println("No tab selected");
        }
        // same screen, do nothing
        else if(tab.getText().equals("Person Detail")){
            System.out.println(tab.getText());
        }
        // go to audit trail screen
        else if(tab.getText().equals("Audit Trail")){
            // only switch to the audit trail list if it is a valid id
            if(id.getText().isEmpty() == false){
                // switch to the audit view, with the person id
                MainController.getInstance().switchView(ScreenType.AUDIT_LIST, detailPerson);
            }
        }
    }



    /** Button events **/
    @FXML
    void cancelButtonClicked(ActionEvent event) {
        //TODO: Create a method for this in HelperFunctions class to clean up
        // confirmation message
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No, return to detail view", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to cancel", no, yes);
        alert.setHeaderText("Changes will NOT be saved if you cancel!!");
        alert.setTitle("Form Warning");
        Optional<ButtonType> result = alert.showAndWait();
        // if yes is selected, will switch to the list view
        if(result.orElse(yes) == yes){
            MainController.getInstance().switchView(ScreenType.PERSON_LIST);
        }
    }

    @FXML
    void saveButtonClicked(ActionEvent event) {
        if(validateUserInput() == false){
            errorPopUp("Form Error", "Your input did not pass our validation check...", "=== Input Rules ===\n1. First and last name are not empty\n2. A valid birth date is applied");
            return;
        }

        /** Updating person **/
        if(isUpdatingPerson){
            LOGGER.info("UPDATING " +firstName.getText() +" " +lastName.getText());

            int[] bitFieldsChanged = {0, 0, 0};
            setBitField(bitFieldsChanged);

            // create the json object string to pass to updatePerson
            JSONObject jsonObjectChangedValues = jsonChangedValues(bitFieldsChanged);
            String jsonStrBody = jsonObjectChangedValues.toString();

            // updates the record in the db
            personGateway.updatePerson(MainController.getInstance().getSession().getSessionID(), detailPerson.getIdentifcationNumber(), jsonStrBody);
        }
        /** Adding a new person **/
        else{
            System.out.println("\n\n ADDING \n\n");
            System.exit(1);
            LOGGER.info("CREATING " +firstName.getText() +" " +lastName.getText());
            int[] bitFieldsChanged = {1, 1, 1};
            JSONObject jsonObjectChangedValues = jsonChangedValues(bitFieldsChanged);
            String jsonStrBody = jsonObjectChangedValues.toString();

            // insert into the db
            personGateway.addPerson(MainController.getInstance().getSession().getSessionID(), jsonStrBody);
        }

        // go to the the list view
        MainController.getInstance().switchView(ScreenType.PERSON_LIST);
    }

    /** returns a json obj of the values that have been changed **/
    private JSONObject jsonChangedValues(int[] bitFields){
        // creates the json body
        JSONObject obj = new JSONObject();

        // first name
        if(bitFields[0] == 1)
            obj.put("firstName", firstName.getText());
        // last name
        if(bitFields[1] == 1)
            obj.put("lastName", lastName.getText());
        // dob
        if(bitFields[2] == 1)
            obj.put("dob", dob.getValue().toString());

        return obj;
    }

    /** Sets bitFields arrays of the form values that have been changed **/
    private void setBitField(int[] bitFields){
        // firstname
        if(!this.detailPerson.getFirstName().equals(firstName.getText()))
            bitFields[0] = 1;
        // lastname
        if(!this.detailPerson.getLastName().equals(lastName.getText()))
            bitFields[1] = 1;
        // dob
        if( !dob.getValue().toString().equals(this.detailPerson.getDateOfBirth().toString()) )
            bitFields[2] = 1;
    }

    /** Initializable **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(isUpdatingPerson == true){ // Update the form with the person passed in
            try {
                LOGGER.info("READING " +detailPerson.toString());

                // sets the form values to the person attributes
                firstName.setText(detailPerson.getFirstName());
                lastName.setText(detailPerson.getLastName());
                id.setText(String.valueOf( detailPerson.getIdentifcationNumber()) );
                age.setText(String.valueOf(detailPerson.getAge()));
                dob.setValue(detailPerson.getDateOfBirth());
            }
            catch (NullPointerException n){
                LOGGER.error("Unable to load the person passed in...");
            }
        }
        else{ // Create a new person object
            detailPerson = new Person();
        }
    }

    /** Validates the user input **/
    private boolean validateUserInput(){
        // first name and last name are both non-empty strings
        if(firstName.getText().isEmpty() == false && lastName.getText().isEmpty() == false){
           // date picker is set to a value
           if(dob.getValue() != null){
               return true;
           }
        }

        return false;
    }

    /** Sets the detailPerson attributes to the values in the javafx form **/
    private void setPersonObjectToFormValues(){
        this.detailPerson.setFirstName(firstName.getText());
        this.detailPerson.setLastName(lastName.getText());
        this.detailPerson.setDateOfBirth(dob.getValue());
        this.detailPerson.setAge(dob.getValue());
    }
}