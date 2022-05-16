package springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import springboot.model.Person;

public interface PaginationRepo extends PagingAndSortingRepository<Person, Integer> {

    Page<Person> findByLastNameStartingWith(String lastName, Pageable pageable);
}
