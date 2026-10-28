package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.repository.ProfileRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final ProfileRepository profileRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            CategoryRepository categoryRepository,
            ProfileRepository profileRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.profileRepository = profileRepository;
    }


    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    public Recipe createRecipe(RecipeInputDto recipeInputDto, String keycloakId) {
        Recipe recipe = new Recipe();
        Profile profile = profileRepository.findByUser_KeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found."
                ));

        recipe.setOwner(profile);
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
                        "We couldn't find the recipe with id: " + id
                ));
    }
    @Transactional
    public Recipe updateRecipe(
            Long id,
            RecipeInputDto recipeInputDto,
            String keycloakId,
            boolean isAdmin) {
        Recipe recipe = getRecipeById(id);
        checkRecipeAccess(recipe, keycloakId, isAdmin);
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
    @Transactional
    public void deleteRecipe(Long id, String keycloakId, boolean isAdmin) {
        Recipe recipe = getRecipeById(id);
        checkRecipeAccess(recipe, keycloakId, isAdmin);
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

    public List<Recipe> getRecipesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category not found with id: " + categoryId
            );
        }

        return recipeRepository.findByCategories_Id(categoryId);
    }
    private void checkRecipeAccess(Recipe recipe, String keycloakId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }

        if (recipe.getOwner() == null ||
                !recipe.getOwner().getUser().getKeycloakId().equals(keycloakId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only edit or delete your own recipes"
            );
        }
    }
    @Transactional(readOnly = true)
    public List<Recipe> getMyRecipes(String keycloakId) {
        return recipeRepository.findByOwner_User_KeycloakId(keycloakId);
    }
}