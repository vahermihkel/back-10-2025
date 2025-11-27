package ee.mihkel.veebipood.repository;

import ee.mihkel.veebipood.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByActiveTrue();

}
