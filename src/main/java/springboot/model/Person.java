package springboot.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "first_name", nullable = false)
    @Length(min = 1, max = 100)
    @NotBlank(message = "First name is required.")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Length(min = 1, max = 100)
    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Column(name = "dob", nullable = false)
    @NotNull(message = "The date of birth is required.")
    @Past(message = "The dob has to be in the past")
    private LocalDate dob;

    @Transient
    private int age; // not stored in the db, calculated in this class

    /** To String method **/
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", age=" + age +
                '}';
    }


    // calculates age after a record is inserted into the db
    @PostLoad
    public void calculateAge(){
        this.age = Period.between(this.getDob(), LocalDate.now()).getYears();
    }

    /** Getters and Setters **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    /************ VALID CHECKERS ***************/
    public static boolean isValidName(String name){
        // has to be between 1 and 100 characters
        return name.length() >= 1 && name.length() <= 100;
    }
    public static boolean isValidDob(LocalDate localDate){
        if(localDate.isBefore(LocalDate.now()) == true){
            return true;
        }
       return false;
    }

}
