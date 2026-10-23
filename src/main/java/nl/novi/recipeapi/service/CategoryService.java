
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.CategoryInputDto;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with id: " + id
                ));
    }

    public Category createCategory(CategoryInputDto inputDto) {
        Category category = new Category();
        category.setName(inputDto.getName());

        return categoryRepository.save(category);
    }
}
