package springboot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import springboot.model.Person;

import java.util.List;

public interface PersonRepo extends JpaRepository<Person, Integer>, PagingAndSortingRepository<Person, Integer>{
//    public interface PersonRepo extends PagingAndSortingRepository<Person, Integer>{
//    @Query("SELECT * FROM People p WHERE LOWER( p.last_name ) LIKE '%?1%'")
//    List<Person> searchByLastName(String lastName);
}
