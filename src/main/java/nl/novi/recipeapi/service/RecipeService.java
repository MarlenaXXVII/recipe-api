package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import nl.novi.recipeapi.dto.RecipeInputDto;

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
}