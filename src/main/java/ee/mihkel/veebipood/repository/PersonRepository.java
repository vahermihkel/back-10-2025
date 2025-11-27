package ee.mihkel.veebipood.repository;

import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person,Long> {
    List<Person> findAllByOrderByIdAsc();

    Person findByEmail(String email);

//    @Query("SELECT p FROM products WHERE p.email")
//    Person mySpecialFuntion();
}
