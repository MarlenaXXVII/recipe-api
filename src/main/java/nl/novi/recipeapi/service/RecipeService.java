package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import nl.novi.recipeapi.dto.RecipeInputDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    public Recipe createRecipe(RecipeInputDto recipeInputDto) {

        Recipe recipe = new Recipe();

        recipe.setTitle(recipeInputDto.getTitle());
        recipe.setDescription(recipeInputDto.getDescription());
        recipe.setPreparationTime(recipeInputDto.getPreparationTime());
        recipe.setCookingTime(recipeInputDto.getCookingTime());
        recipe.setServings(recipeInputDto.getServings());

        return recipeRepository.save(recipe);
    }
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found with id: " + id
                ));
    }
    public Recipe updateRecipe(Long id, RecipeInputDto recipeInputDto) {
        Recipe recipe = getRecipeById(id);

        recipe.setTitle(recipeInputDto.getTitle());
        recipe.setDescription(recipeInputDto.getDescription());
        recipe.setPreparationTime(recipeInputDto.getPreparationTime());
        recipe.setCookingTime(recipeInputDto.getCookingTime());
        recipe.setServings(recipeInputDto.getServings());

        return recipeRepository.save(recipe);
    }
}