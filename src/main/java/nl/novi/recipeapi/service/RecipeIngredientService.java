
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.RecipeIngredientInputDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.model.RecipeIngredient;
import nl.novi.recipeapi.repository.IngredientRepository;
import nl.novi.recipeapi.repository.RecipeIngredientRepository;
import nl.novi.recipeapi.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeIngredientService(
            RecipeIngredientRepository recipeIngredientRepository,
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public RecipeIngredient addIngredientToRecipe(
            Long recipeId, RecipeIngredientInputDto inputDto) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found with id: " + recipeId
                ));

        Ingredient ingredient = ingredientRepository.findById(inputDto.getIngredientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ingredient not found with id: " + inputDto.getIngredientId()
                ));

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(inputDto.getAmount());
        recipeIngredient.setUnit(inputDto.getUnit());
        recipeIngredient.setNotes(inputDto.getNotes());

        return recipeIngredientRepository.save(recipeIngredient);
    }

    public List<RecipeIngredient> getIngredientsByRecipeId(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Recipe not found with id: " + recipeId
            );
        }

        return recipeIngredientRepository.findByRecipeId(recipeId);
    }
}
