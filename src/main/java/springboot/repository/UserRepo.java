package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springboot.model.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    // returns the user object if they have the same username and password
    @Query("SELECT u FROM User u WHERE u.login = ?1 AND u.password = ?2")
    Optional<User> findByLoginAndPassword(String login, String password);
}