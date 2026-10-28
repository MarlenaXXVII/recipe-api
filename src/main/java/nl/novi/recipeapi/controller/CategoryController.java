package nl.novi.recipeapi.controller;

import nl.novi.recipeapi.dto.CategoryInputDto;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody CategoryInputDto inputDto) {

        Category savedCategory = categoryService.createCategory(inputDto);

        return ResponseEntity
                .created(URI.create("/categories/" + savedCategory.getId()))
                .body(savedCategory);
    }
}
