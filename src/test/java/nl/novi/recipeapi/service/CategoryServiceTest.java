
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.CategoryInputDto;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategoriesReturnsAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Diner");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Diner", result.get(0).getName());

        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryByIdReturnsCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Diner");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Diner", result.getName());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryByIdThrowsExceptionWhenNotFound() {
        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> categoryService.getCategoryById(999L)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void createCategorySavesCategory() {
        CategoryInputDto inputDto = new CategoryInputDto();
        inputDto.setName("Ontbijt");

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(2L);
                    return category;
                });

        Category result = categoryService.createCategory(inputDto);

        assertEquals(2L, result.getId());
        assertEquals("Ontbijt", result.getName());

        verify(categoryRepository).save(any(Category.class));
    }
}
