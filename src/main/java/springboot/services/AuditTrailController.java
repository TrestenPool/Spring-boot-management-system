package springboot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.model.AuditTable;
import springboot.model.Person;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.AuditTableRepo;
import springboot.repository.PersonRepo;
import springboot.repository.SessionRepo;
import springboot.repository.UserRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuditTrailController {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
   AuditTableRepo auditTableRepo;
    SessionRepo sessionRepo;
    UserRepo userRepo;
    PersonRepo personRepo;

    public AuditTrailController(AuditTableRepo auditTableRepo, SessionRepo sessionRepo, UserRepo userRepo, PersonRepo personRepo) {
        this.auditTableRepo = auditTableRepo;
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
        this.personRepo = personRepo;
    }

    /****** Fetch audit trails *******/
    // returns list of all the given person id's audit trail rails records, ordered by when they occured
   @GetMapping("/people/{person_id}/audittrail")
   @ResponseBody
    public ResponseEntity<List<AuditTable>> fetchAuditTrail(
           @RequestHeader Map<String,String> headers,      // header information
           @PathVariable("person_id") int personID )        // person ID passed into the URL
    {
        // return 401 if header is invalid
        if(isHeaderValid(headers) == false){
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }


        // grab all the records by person id
        List<AuditTable> auditTableList = auditTableRepo.customQuery(personID);

        // return 404 if the personID was not found in the list
        if(auditTableList.isEmpty() == true){
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        // return response
        return new ResponseEntity<>(auditTableList, HttpStatus.valueOf(200));
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
}
