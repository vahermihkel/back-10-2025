package ee.mihkel.veebipood.controller;

import ee.mihkel.veebipood.entity.Category;
import ee.mihkel.veebipood.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    // http://localhost:8080/categories
    @GetMapping("categories")
    public ResponseEntity<List<Category>> getCategories(){
        return ResponseEntity.ok().body(categoryRepository.findAll()); // SELECT * FROM categories;
    }

    // http://localhost:8080/categories?id=1
    @DeleteMapping("categories")
    public ResponseEntity<List<Category>> deleteCategory(@RequestParam Long id){
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().body(categoryRepository.findAll());
    }

    // http://localhost:8080/categories
    @PostMapping("categories") // lisab siis, kui sellist ID-d pole olemas
    public ResponseEntity<List<Category>> addCategory(@RequestBody Category category){
        if (category.getId() != null){
            throw new RuntimeException("Cannot add with ID");
        }
        categoryRepository.save(category);
        return ResponseEntity.status(201).body(categoryRepository.findAll()); // SELECT * FROM categories;
    }
}
