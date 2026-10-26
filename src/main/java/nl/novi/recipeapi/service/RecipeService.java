package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import nl.novi.recipeapi.dto.RecipeInputDto;
import org.springframework.web.server.ResponseStatusException;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.repository.CategoryRepository;
import java.util.HashSet;
import java.util.Set;


import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
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
        recipe.setImage(recipeInputDto.getImage());
        recipe.setDifficulty(recipeInputDto.getDifficulty());
        recipe.setInstructions(recipeInputDto.getInstructions());
        recipe.setCalories(recipeInputDto.getCalories());
        recipe.setProtein(recipeInputDto.getProtein());
        recipe.setCarbs(recipeInputDto.getCarbs());
        recipe.setFat(recipeInputDto.getFat());
        recipe.setCategories(getCategoriesByIds(recipeInputDto.getCategoryIds()));

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
        recipe.setImage(recipeInputDto.getImage());
        recipe.setDifficulty(recipeInputDto.getDifficulty());
        recipe.setInstructions(recipeInputDto.getInstructions());
        recipe.setCalories(recipeInputDto.getCalories());
        recipe.setProtein(recipeInputDto.getProtein());
        recipe.setCarbs(recipeInputDto.getCarbs());
        recipe.setFat(recipeInputDto.getFat());

        if (recipeInputDto.getCategoryIds() != null) {
            recipe.setCategories(getCategoriesByIds(recipeInputDto.getCategoryIds()));
        }

        return recipeRepository.save(recipe);
    }
    public void deleteRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    private Set<Category> getCategoriesByIds(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();

        if (categoryIds == null) {
            return categories;
        }

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Category not found with id: " + categoryId
                    ));

            categories.add(category);
        }

        return categories;
    }

}