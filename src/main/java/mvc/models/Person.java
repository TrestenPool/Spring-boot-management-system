package mvc.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class Person {
    private static final Logger logger = LogManager.getLogger();
    /** Variables **/
    private String firstName;
    private String lastName;
    private int age;
    private int identifcationNumber;
    private LocalDate dateOfBirth;

    /** Constructor **/
    public Person(String firstName, String lastName, int identifcationNumber, LocalDate dateOfBirth) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setIdentifcationNumber(identifcationNumber);
        this.setDateOfBirth(dateOfBirth);
        this.setAge(dateOfBirth);
    }


    // empty constructor
    public  Person(){
        this.firstName = "";
        this.lastName = "";
        this.age = 0;
        this.identifcationNumber = 0;
        this.dateOfBirth = LocalDate.of(1,1,1);
    }

    /** toString method **/
    @Override
    public String toString() {
        return firstName +" " +lastName;
    }

    /** fromJSONObject method **/
    public static Person fromJSONObject(JSONObject json) {
        try {
            // creates the person object by parsing the json object
//            Person person = new Person(json.getString("first_name"), json.getString("last_name"), json.getInt("age"), json.getInt("id"), LocalDate.parse(json.getString("date_of_birth")));
            Person person = new Person(json.getString("first_name"), json.getString("last_name"), json.getInt("id"), LocalDate.parse(json.getString("date_of_birth")));
            return person;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    /** converts result set to a person object **/
    public static Person fromResultSet(ResultSet rs) throws SQLException{
        Person person = new Person();
        // sets the first and last name
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        // sets the id
        person.setIdentifcationNumber(rs.getInt("id"));

        // ToDo: set the age and the dob to the result set argument
        person.setAge(LocalDate.of(1901,1,1));
        person.setDateOfBirth(LocalDate.of(1901,1,1));

        return person;
    }

    /** Getters and Setters **/
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if( firstName.length() > 0 && firstName.length() < 100 )
            this.firstName = firstName;
        else
            this.firstName = "DEFAULT_NAME";
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if( lastName.length() > 0 && lastName.length() < 100 )
            this.lastName = lastName;
        else
            this.lastName = "DEFAULT_NAME";
    }
    public int getAge() {
        return age;
    }
    public void setAge(LocalDate dateOfBirth) {
        Period period = Period.between(dateOfBirth, LocalDate.now());
        // the birthdate is after today
        if(period.isNegative() == true){
            logger.error("ERROR, BIRTHDATE IS AFTER CURRENT DATE\n" +"birthdate " +dateOfBirth.toString() +" is after current date " +LocalDate.now());
        }
        this.age = period.getYears();
    }

    public int getIdentifcationNumber() {
        return identifcationNumber;
    }
    public void setIdentifcationNumber(int identifcationNumber) {
        this.identifcationNumber = identifcationNumber;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
