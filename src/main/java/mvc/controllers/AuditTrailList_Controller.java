package mvc.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import mvc.gateway.PersonGatewaySpringBoot;
import mvc.models.AuditTrailRecord;
import mvc.models.Person;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AuditTrailList_Controller implements Initializable, MyController{
    @FXML
    private ListView<AuditTrailRecord> auditListView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab personDetailTab;

    @FXML
    private Tab auditTrailTab;

    private ArrayList<AuditTrailRecord> auditTrailRecordArrayList;
    private Person passingPerson;


    // when person detail tab is clicked, go back to the person detail view
    @FXML
    void showDetailView(MouseEvent event){
        Tab tab = tabPane.getSelectionModel().getSelectedItem();

        if(tab == null){
        }
        else if(tab.getText().equals("Person Detail")){
            // switch to the detail view again
            MainController.getInstance().switchView(ScreenType.PERSON_DETAIL, passingPerson);
        }
        else if(tab.getText().equals("Audit Trail")){
        }
    }

    // constructor
    public AuditTrailList_Controller(ArrayList<AuditTrailRecord> auditTrailRecordArrayList, Person passingPerson) {
        this.auditTrailRecordArrayList = auditTrailRecordArrayList;
        this.passingPerson = passingPerson;
        this.auditListView = new ListView<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       updatePersonList();
    }

    // sets the list view with the contents in the array list auditTrailRecordArrayList
    void updatePersonList(){
        ObservableList<AuditTrailRecord> tempList = FXCollections.observableArrayList(auditTrailRecordArrayList);
        auditListView.setItems(tempList);
    }


}
