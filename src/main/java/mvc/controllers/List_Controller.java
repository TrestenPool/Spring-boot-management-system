package mvc.controllers;

import com.sun.javafx.scene.control.InputField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mvc.gateway.PersonGatewaySpringBoot;
import mvc.gateway.Session;
import mvc.models.PagingResults;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Slice;
import springboot.services.PersonController;

import static miscellaneous.myUtil.HelperFunctions.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class List_Controller implements Initializable, MyController {
    /**
     * Variables
     **/
    public static final Logger LOGGER = LogManager.getLogger(); // logger
    private ArrayList<Person> people;
    private PersonGatewaySpringBoot personGateway; // the reference to the person gateway
    private PagingResults pagingResults;
    /**
     * List Object
     **/
    @FXML
    private ListView<Person> peopleList;
    @FXML
    private Button nextBtn;
    @FXML
    private Button prevBtn;
    @FXML
    private Button firstBtn;
    @FXML
    private Button lastBtn;
    @FXML
    private Label fetchedRecordsLabel;
    @FXML
    private TextField searchInput;
    

    /**
     * Constructor
     **/
    public List_Controller() {

    }

    /**
     * Initializable
     **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.personGateway = new PersonGatewaySpringBoot();
        updatePersonList(MainController.getInstance().getSession().getCurrentPage());
    }

    /**
     * Action Events
     **/
    // Switches to the detail view
    @FXML
    void addPersonButtonClicked(ActionEvent event) {
        MainController.getInstance().switchView(ScreenType.PERSON_DETAIL);
    }

    @FXML
    void firstPageButtonClicked(ActionEvent event) {
        updatePersonList(0);
    }

    @FXML
    void lastPageButtonClicked(ActionEvent event) {
        updatePersonList(-1);
    }

    @FXML
    void nextPageButtonClicked(ActionEvent event) {
        updatePersonList("next");
    }

    @FXML
    void prevPageButtonClicked(ActionEvent event) {
        updatePersonList("Prev");
    }

    @FXML
    void searchButtonClicked(MouseEvent event) {
        String searchQuery = searchInput.getText();
    }
    
    @FXML
    void clickPerson(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Person passingPerson = peopleList.getSelectionModel().getSelectedItem(); // gets the selected person

            // user did not double click on person
            if (passingPerson == null) {
                LOGGER.info("You did not double click on a person!!");
            }
            // user double clicked on a person, switch to the detail view
            else {
                MainController.getInstance().switchView(ScreenType.PERSON_DETAIL, passingPerson);
            }
        }
    }

    // Deletes a person in the list if they are selectedceWordForwardAction)
    @FXML
    void deletePersonButtonClicked(ActionEvent event) {
        // checks what is selected in the list view and stores in passing person
        Person passingPerson = peopleList.getSelectionModel().getSelectedItem();
        // No person is selected
        if (passingPerson == null) {
            warningPopUp("Warning", "No Person Selected", "Select a person in the list view and\nclick <Delete Person>\nto delete the selected person");
        }
        // Deletes the person that was selected
        else {
            /*** Deleting **/
            personGateway.deletePerson(MainController.getInstance().getSession().getSessionID(), passingPerson.getIdentifcationNumber());
        }
    }

    // TODO: refreshes the person list
    void updatePersonList() {
        updatePersonList(0);
    }

    void updatePersonList(int pageNumber) {
        Session session = MainController.getInstance().getSession();
        if (this.personGateway == null) {
            this.personGateway = new PersonGatewaySpringBoot();
        }
        if(pageNumber == -1 && this.pagingResults != null) {
            pageNumber = this.pagingResults.getTotalPages() - 1;
        }
        this.pagingResults = personGateway.fetchPeople(session.getSessionID(), pageNumber);
        this.people = this.pagingResults.getValues();
        session.setCurrentPage(pageNumber);
        String fetchedLabelText = String.format("Fetched records %d to %d out of %d records",
                this.pagingResults.getOffset() + 1, this.pagingResults.getOffset() + this.pagingResults.getNumberOfElements(), this.pagingResults.getTotalElements());
        if (peopleList != null) {
            ObservableList<Person> tempList = FXCollections.observableArrayList(people);
            peopleList.setItems(tempList);
        }
        if (fetchedRecordsLabel != null)
            fetchedRecordsLabel.setText(fetchedLabelText);
        if (prevBtn != null) {
            prevBtn.setDisable(pageNumber == 0 || this.people.size() == 0);
        }
        if (nextBtn != null) {
            nextBtn.setDisable(this.pagingResults.isLast() || this.people.size() == 0);
        }
        if (firstBtn != null) {
            firstBtn.setDisable(this.people.size() == 0);
        }
        if (lastBtn != null) {
            lastBtn.setDisable(this.people.size() == 0);
        }
    }

    void updatePersonList(String relativePage) {
        if (relativePage.equals("next")) {
            updatePersonList(MainController.getInstance().getSession().getCurrentPage() + 1);
        } else {
            updatePersonList(MainController.getInstance().getSession().getCurrentPage() - 1);
        }
    }


    /**
     * Getters and setters
     **/
    public ListView<Person> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(ListView<Person> peopleList) {
        this.peopleList = peopleList;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }
}
