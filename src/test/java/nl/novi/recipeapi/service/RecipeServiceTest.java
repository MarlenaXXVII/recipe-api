
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.RecipeInputDto;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getAllRecipesReturnsAllRecipes() {

        Recipe recipe = new Recipe();
        recipe.setTitle("Pasta Carbonara");

        when(recipeRepository.findAll())
                .thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getAllRecipes();

        assertEquals(1, result.size());
        assertEquals("Pasta Carbonara", result.get(0).getTitle());
    }

    @Test
    void createRecipeSavesRecipe() {

        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Pasta Carbonara");
        inputDto.setDescription("Een Italiaans pastagerecht");
        inputDto.setPreparationTime(15);
        inputDto.setCookingTime(20);
        inputDto.setServings(2);

        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(1L);
        savedRecipe.setTitle("Pasta Carbonara");

        when(recipeRepository.save(any(Recipe.class)))
                .thenReturn(savedRecipe);

        Recipe result = recipeService.createRecipe(inputDto);

        assertEquals(1L, result.getId());
        assertEquals("Pasta Carbonara", result.getTitle());

        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void getRecipeByIdReturnsRecipe() {

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta Carbonara");

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getRecipeById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Pasta Carbonara", result.getTitle());

        verify(recipeRepository).findById(1L);
    }

    @Test
    void getRecipeByIdThrowsExceptionWhenNotFound() {

        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getRecipeById(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository).findById(999L);
    }

    @Test
    void updateRecipeUpdatesExistingRecipe() {

        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(1L);
        existingRecipe.setTitle("Pasta Carbonara");
        existingRecipe.setServings(2);

        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Vegetarische Pasta");
        inputDto.setDescription("Pasta met groenten");
        inputDto.setPreparationTime(15);
        inputDto.setCookingTime(20);
        inputDto.setServings(4);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(existingRecipe));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.updateRecipe(1L, inputDto);

        assertEquals("Vegetarische Pasta", result.getTitle());
        assertEquals("Pasta met groenten", result.getDescription());
        assertEquals(4, result.getServings());

        verify(recipeRepository).findById(1L);
        verify(recipeRepository).save(existingRecipe);
    }

    @Test
    void deleteRecipeDeletesExistingRecipe() {

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta Carbonara");

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(1L);

        verify(recipeRepository).findById(1L);
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void updateRecipeThrowsExceptionWhenNotFound() {
        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Pasta Carbonara");

        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.updateRecipe(999L, inputDto)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository).findById(999L);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void deleteRecipeThrowsExceptionWhenNotFound() {
        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository).findById(999L);
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void createRecipeWithCategoriesSavesCategories() {
        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Pasta");
        inputDto.setCategoryIds(Set.of(1L, 2L));

        Category dinner = new Category();
        dinner.setId(1L);
        dinner.setName("Diner");

        Category lunch = new Category();
        lunch.setId(2L);
        lunch.setName("Lunch");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(dinner));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(lunch));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(inputDto);

        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains(dinner));
        assertTrue(result.getCategories().contains(lunch));

        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void createRecipeThrowsExceptionWhenCategoryNotFound() {
        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Pasta");
        inputDto.setCategoryIds(Set.of(999L));

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.createRecipe(inputDto)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void getRecipesByCategoryReturnsRecipes() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta met tomatensaus");

        when(categoryRepository.existsById(2L))
                .thenReturn(true);

        when(recipeRepository.findByCategories_Id(2L))
                .thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getRecipesByCategory(2L);

        assertEquals(1, result.size());
        assertEquals("Pasta met tomatensaus", result.get(0).getTitle());

        verify(recipeRepository).findByCategories_Id(2L);
    }

    @Test
    void getRecipesByCategoryThrowsExceptionWhenCategoryNotFound() {
        when(categoryRepository.existsById(999L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getRecipesByCategory(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository, never()).findByCategories_Id(999L);
    }

    @Test
    void getRecipesByCategoryReturnsEmptyList() {
        when(categoryRepository.existsById(3L))
                .thenReturn(true);

        when(recipeRepository.findByCategories_Id(3L))
                .thenReturn(List.of());

        List<Recipe> result = recipeService.getRecipesByCategory(3L);

        assertTrue(result.isEmpty());

        verify(recipeRepository).findByCategories_Id(3L);
    }

}
