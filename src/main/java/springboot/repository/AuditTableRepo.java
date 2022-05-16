package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springboot.model.AuditTable;
import springboot.model.Person;
import springboot.model.Session;
import springboot.model.User;

import java.util.List;

public interface AuditTableRepo extends JpaRepository<AuditTable, Integer> {
    // returns a list of sessions, finds by token
    @Query(value = "SELECT * from audit_table a WHERE a.person_id = ?1 ORDER BY a.when_occured", nativeQuery = true)
    List<AuditTable> customQuery(int person_id);
}
