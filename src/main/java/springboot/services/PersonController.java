package springboot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springboot.model.AuditTable;
import springboot.model.Person;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@Validated
public class PersonController {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int PAGE_SIZE = 10;

    @Autowired
    PersonRepo personRepo;
    SessionRepo sessionRepo;
    AuditTableRepo auditTableRepo;
    UserRepo userRepo;
    PaginationRepo paginationRepo;

    public PersonController(PersonRepo personRepo, SessionRepo sessionRepo, AuditTableRepo auditTableRepo, UserRepo userRepo, PaginationRepo paginationRepo) {
        this.personRepo = personRepo;
        this.sessionRepo = sessionRepo;
        this.auditTableRepo = auditTableRepo;
        this.userRepo = userRepo;
        this.paginationRepo = paginationRepo;
    }

    /** 2. Get People **/
    // pass in authorization header
    @GetMapping("/people")
    public ResponseEntity<Slice<Person>> fetchPeople(
            @RequestParam(required = false) Optional<Integer> pageNum,
            @RequestParam(required = false) String lastName,
            @RequestHeader Map<String,String> headers)      // header information
    {
        // header is not valid
        if( !isHeaderValid(headers) ) {
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        if(pageNum.isPresent()){
            Integer realPageNum = pageNum.get();

            if(realPageNum < 0)
                realPageNum = 0;

            // sets the page number
            Pageable pageable = PageRequest.of(realPageNum, PAGE_SIZE);

            Page<Person> personsPage;

            if(lastName == null)
                personsPage = paginationRepo.findAll(pageable);
            else
                personsPage = paginationRepo.findByLastNameStartingWith(lastName, pageable);
            return new ResponseEntity<>(personsPage, HttpStatus.valueOf(200));
        }

        Pageable pageable = PageRequest.of(1, PAGE_SIZE);
        Page<Person> personsPage;
        personsPage = paginationRepo.findAll(pageable);
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }


    /** 3. Insert person **/
    // pass in authorization header
    // pass in valid person object
    // returns the person that was saved in the db
    @PostMapping("/people")
    public ResponseEntity<Person> insertPerson(
            @RequestHeader Map<String,String> headers,      // header information
            @Valid @RequestBody Person person )             // person passed into the request body
    {
        // check if the header is valid
        if(isHeaderValid(headers) == false){
            LOGGER.info("Authorization header missing or empty");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        // insert into db and calculate the age
        personRepo.save(person);
        person.calculateAge();

        // inserts into the audit table
        insertIntoAuditTable(headers, "added", person);

        // return 200 and the person that was saved
       return new ResponseEntity<>(person, HttpStatus.valueOf(200));
    }

    /** 4. Update person **/
    // Todo: be able to accept parts of a person, of only the parts they want changed
    @PutMapping("/people/{personID}")
    public ResponseEntity<Person> updatePerson(
            @RequestHeader Map<String,String> headers,              // header info
            @RequestBody Person changedPerson,                      // person object with attributes of what is changed
            @PathVariable("personID") int personID)                 // person ID passed into the URL
    {
        // session key not valid, return 401
        if(isHeaderValid(headers) == false){
            LOGGER.info("Invalid session key");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        // attempt to retrieve the person in the db by the id
        Optional<Person> optionalPerson = personRepo.findById(personID);

        // person with personID not found in people db. Return 404
        if(optionalPerson.isEmpty()){
            LOGGER.info("PersonID: " +personID +" is not present in the db");
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        System.out.println("Changed person = " +changedPerson.toString());

        // person we will be saving our changes to and updating in the db
       Person person = optionalPerson.get();
        String change_msg = new String();

        // changing the first name if valid
        if(changedPerson.getFirstName() != null && Person.isValidName(changedPerson.getFirstName()) == true){
            // audit trail
            change_msg = String.format("first_name changed from %s",  person.getFirstName());
            insertIntoAuditTable(headers, change_msg, person);
            change_msg = String.format("first_name changed to %s", changedPerson.getFirstName());
            insertIntoAuditTable(headers, change_msg, person);

            // update value
            person.setFirstName(changedPerson.getFirstName());
        }
        // changing last name if valid
        if(changedPerson.getLastName() != null && Person.isValidName(changedPerson.getLastName()) == true){
            // audit trail
            change_msg = String.format("last_name changed from %s",  person.getLastName());
            insertIntoAuditTable(headers, change_msg, person);
            change_msg = String.format("last_name changed to %s", changedPerson.getLastName());
            insertIntoAuditTable(headers, change_msg, person);

            // update value
            person.setLastName(changedPerson.getLastName());
        }
        // changing dob if valid
        if(changedPerson.getDob() != null && Person.isValidDob(changedPerson.getDob()) == true){
            // write in audit table
            change_msg = String.format("dob changed from %s", person.getDob().toString());
            insertIntoAuditTable(headers, change_msg, person);
            change_msg = String.format("dob changed to %s", changedPerson.getDob().toString());
            insertIntoAuditTable(headers, change_msg, person);

            // calculate new age
            person.calculateAge();
            // update the value
            person.setDob(changedPerson.getDob());
        }

        // save the person that was passed in to update the person
        personRepo.save(person);

        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    /** 5. Delete Person **/
    @DeleteMapping("/people/{personID}")
    public ResponseEntity<String> DeletePerson(@RequestHeader Map<String,String> headers, @PathVariable("personID") int personID)                 // person ID passed into the URL
    {
        if( !isHeaderValid(headers) ){
            return new ResponseEntity<>("", HttpStatus.valueOf(401));
        }

        Optional<Person> tempPerson = personRepo.findById(personID);
        // person not found
        if( !tempPerson.isPresent() ){
            return new ResponseEntity<>("", HttpStatus.valueOf(404));
        }

        LOGGER.info("Deleting: " + tempPerson.toString());
        personRepo.deleteById(personID);
        return new ResponseEntity<>("", HttpStatus.valueOf(200));
    }

    /** 6. Fetch Person **/
    @RequestMapping(method = GET, value = "/people/{personID}")
    @ResponseBody
    public ResponseEntity<Person> getPerson(
            @RequestHeader Map<String,String> headers,      // header information
            @PathVariable("personID") int personID)     // personID passed into URL
    {
        // invalid header information
        if(isHeaderValid(headers) == false){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        Optional<Person> person = personRepo.findById(personID);
        // person not found
        if( !person.isPresent() ){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        // person was found
        return new ResponseEntity<Person>(person.get(), HttpStatus.valueOf(200));
    }


    // returns true if authorization header in the session table
    // returns false if authorization header is not in the session table
    public boolean isHeaderValid(Map<String, String> headers){
        String sessionToken = headers.get("authorization");
        // session token missing or null
        if(sessionToken == null || sessionToken.isEmpty() == true){
            LOGGER.info("Authorization header missing or empty");
            return false;
        }

        // searches for the session in the session db
        Session session = sessionRepo.findByToken(sessionToken);
        // returns true if session is not null
        // returns false if session is null
        return session != null;
    }

    // writes into the audit table
    public void insertIntoAuditTable(Map<String,String> headers, String change_msg, Person person) {
        String sessionToken = headers.get("authorization");
        Session session = sessionRepo.findByToken(sessionToken);
        Optional<User> userOptional = userRepo.findById(session.getUser().getId());

        // insert into the audit table
        AuditTable auditTable = new AuditTable();
        auditTable.setChangeMsg(change_msg);
        auditTable.setPerson(person);
        auditTable.setChangedBy(userOptional.get());
        auditTableRepo.save(auditTable);
    }
}
