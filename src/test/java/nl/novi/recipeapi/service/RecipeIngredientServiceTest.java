
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.RecipeIngredientInputDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.model.RecipeIngredient;
import nl.novi.recipeapi.repository.IngredientRepository;
import nl.novi.recipeapi.repository.RecipeIngredientRepository;
import nl.novi.recipeapi.repository.RecipeRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientServiceTest {

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeIngredientService recipeIngredientService;

    @Test
    void addIngredientToRecipeSavesRelationship() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setName("Spaghetti");

        RecipeIngredientInputDto inputDto = new RecipeIngredientInputDto();
        inputDto.setIngredientId(2L);
        inputDto.setAmount(200.0);
        inputDto.setUnit("gram");
        inputDto.setNotes("Ongekookt afwegen");

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(ingredientRepository.findById(2L))
                .thenReturn(Optional.of(ingredient));

        when(recipeIngredientRepository.save(any(RecipeIngredient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecipeIngredient result =
                recipeIngredientService.addIngredientToRecipe(1L, inputDto);

        assertEquals(1L, result.getRecipe().getId());
        assertEquals(2L, result.getIngredient().getId());
        assertEquals(200.0, result.getAmount());
        assertEquals("gram", result.getUnit());
        assertEquals("Ongekookt afwegen", result.getNotes());

        verify(recipeIngredientRepository).save(any(RecipeIngredient.class));
    }

    @Test
    void addIngredientToRecipeThrowsExceptionWhenRecipeNotFound() {
        RecipeIngredientInputDto inputDto = new RecipeIngredientInputDto();
        inputDto.setIngredientId(2L);

        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeIngredientService.addIngredientToRecipe(999L, inputDto)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeIngredientRepository, never())
                .save(any(RecipeIngredient.class));
    }

    @Test
    void addIngredientToRecipeThrowsExceptionWhenIngredientNotFound() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        RecipeIngredientInputDto inputDto = new RecipeIngredientInputDto();
        inputDto.setIngredientId(999L);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(ingredientRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeIngredientService.addIngredientToRecipe(1L, inputDto)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeIngredientRepository, never())
                .save(any(RecipeIngredient.class));
    }

    @Test
    void getIngredientsByRecipeIdReturnsIngredients() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setName("Spaghetti");

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(200.0);
        recipeIngredient.setUnit("gram");

        when(recipeRepository.existsById(1L))
                .thenReturn(true);

        when(recipeIngredientRepository.findByRecipeId(1L))
                .thenReturn(List.of(recipeIngredient));

        List<RecipeIngredient> result =
                recipeIngredientService.getIngredientsByRecipeId(1L);

        assertEquals(1, result.size());
        assertEquals("Spaghetti", result.get(0).getIngredient().getName());
        assertEquals(200.0, result.get(0).getAmount());

        verify(recipeIngredientRepository).findByRecipeId(1L);
    }

    @Test
    void getIngredientsByRecipeIdThrowsExceptionWhenRecipeNotFound() {
        when(recipeRepository.existsById(999L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeIngredientService.getIngredientsByRecipeId(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(recipeRepository).existsById(999L);
        verify(recipeIngredientRepository, never()).findByRecipeId(999L);
    }

}
