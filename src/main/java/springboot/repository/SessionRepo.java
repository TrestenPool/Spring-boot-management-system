package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springboot.model.Session;

import java.util.List;

public interface SessionRepo extends JpaRepository<Session, Integer> {

    // returns a list of sessions, finds by token
    @Query("SELECT s FROM Session s where s.token = ?1 ")
    Session findByToken(String token);
}
