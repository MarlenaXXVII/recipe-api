package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.IngredientInputDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.repository.IngredientRepository;
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
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void getAllIngredientsReturnsAllIngredients() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Spaghetti");

        when(ingredientRepository.findAll())
                .thenReturn(List.of(ingredient));

        List<Ingredient> result = ingredientService.getAllIngredients();

        assertEquals(1, result.size());
        assertEquals("Spaghetti", result.get(0).getName());
    }

    @Test
    void getIngredientByIdReturnsIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Spaghetti");

        when(ingredientRepository.findById(1L))
                .thenReturn(Optional.of(ingredient));

        Ingredient result = ingredientService.getIngredientById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Spaghetti", result.getName());

        verify(ingredientRepository).findById(1L);
    }

    @Test
    void getIngredientByIdThrowsExceptionWhenNotFound() {
        when(ingredientRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.getIngredientById(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(ingredientRepository).findById(999L);
    }

    @Test
    void createIngredientSavesIngredient() {
        IngredientInputDto inputDto = new IngredientInputDto();
        inputDto.setName("Tomaat");

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(1L);
        savedIngredient.setName("Tomaat");

        when(ingredientRepository.save(any(Ingredient.class)))
                .thenReturn(savedIngredient);

        Ingredient result = ingredientService.createIngredient(inputDto);

        assertEquals(1L, result.getId());
        assertEquals("Tomaat", result.getName());

        verify(ingredientRepository).save(any(Ingredient.class));
    }
}
