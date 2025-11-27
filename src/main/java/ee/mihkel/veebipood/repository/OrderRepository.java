package ee.mihkel.veebipood.repository;

import ee.mihkel.veebipood.entity.Order;
import ee.mihkel.veebipood.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByPerson(Person person);
}
