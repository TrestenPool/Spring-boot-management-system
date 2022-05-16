package springboot.services;

import miscellaneous.pw_hash.HashUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.SessionRepo;
import springboot.repository.UserRepo;

import java.util.List;
import java.util.Optional;

@RestController
public class SessionController {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    SessionRepo sessionRepo;
    @Autowired
    UserRepo userRepo;

    // constructor
    public SessionController(SessionRepo sessionRepo, UserRepo userRepo) {
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
    }

    /**
     * 1. Creates and returns a session token if the provided credentials(username and HASHED password are found in the user table..
     **/
    // only the username and password are passed into the user object
    // if login&password are correct, will insert a session record into the db, and return the token to the user
    @PostMapping("/login")
    public ResponseEntity<Session> login(@RequestBody User user) {
        if (user.getLogin() == null || user.getPassword() == null || user.getLogin().isEmpty() == true || user.getPassword().isEmpty() == true) {
            System.out.println("login and/or password is missing or empty");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        // Todo: if the user has a session token in the session db already, just return that one instead of creating a new one

        // generates the sha-256 token
        String randomValue = String.valueOf(Math.floor(Math.random() * 10000));
        String token = HashUtils.getCryptoHash(randomValue, "SHA-256");

        // gets all the users in the USER table
        List<User> listOfUsers = userRepo.findAll();

        // pass in login & password, returns a user{id, login, password}
        Optional<User> suspectedUser = userRepo.findByLoginAndPassword(user.getLogin(), user.getPassword());

        // user was found with the credentials
        if (suspectedUser.isPresent()) {
            Session session = new Session();
            session.setUser(suspectedUser.get());
            session.setToken(token);
            sessionRepo.save(session);
            return new ResponseEntity<Session>(session, HttpStatus.valueOf(200));
        }
        // user was not found
        else {
            // otherwise, return 401
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
    }

}
