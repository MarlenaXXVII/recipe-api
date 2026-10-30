package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.RecipeRepository;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.repository.ProfileRepository;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.repository.CategoryRepository;
import nl.novi.recipeapi.dto.RecipeInputDto;
import nl.novi.recipeapi.dto.RecipeIngredientFormDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.model.RecipeIngredient;
import nl.novi.recipeapi.repository.IngredientRepository;
import nl.novi.recipeapi.repository.RecipeIngredientRepository;
import nl.novi.recipeapi.dto.RecipeOutputDto;
import nl.novi.recipeapi.dto.RecipeIngredientOutputDto;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            CategoryRepository categoryRepository,
            ProfileRepository profileRepository,
            IngredientRepository ingredientRepository,
            RecipeIngredientRepository recipeIngredientRepository) {

        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.profileRepository = profileRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }


    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    @Transactional
    public Recipe createRecipe(RecipeInputDto recipeInputDto, String keycloakId) {
        Recipe recipe = new Recipe();

        Profile profile = profileRepository.findByUser_KeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found."
                ));

        recipe.setOwner(profile);
        fillRecipe(recipe, recipeInputDto);
        recipe.setCategories(getCategoriesByIds(recipeInputDto.getCategoryIds()));

        Recipe savedRecipe = recipeRepository.save(recipe);

        saveRecipeIngredients(savedRecipe, recipeInputDto.getIngredients());

        return savedRecipe;
    }
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "We couldn't find the recipe with id: " + id
                ));
    }
    @Transactional(readOnly = true)
    public RecipeOutputDto getRecipeOutputById(Long id) {
        Recipe recipe = getRecipeById(id);

        return toOutputDto(recipe);
    }
    @Transactional
    public Recipe updateRecipe(
            Long id,
            RecipeInputDto recipeInputDto,
            String keycloakId,
            boolean isAdmin) {
        Recipe recipe = getRecipeById(id);
        checkRecipeAccess(recipe, keycloakId, isAdmin);
        fillRecipe(recipe, recipeInputDto);
        if (recipeInputDto.getCategoryIds() != null) {
            recipe.setCategories(getCategoriesByIds(recipeInputDto.getCategoryIds()));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        if (recipeInputDto.getIngredients() != null) {
            recipeIngredientRepository.deleteAll(
                    recipeIngredientRepository.findByRecipeId(id)
            );

            recipeIngredientRepository.flush();

            saveRecipeIngredients(savedRecipe, recipeInputDto.getIngredients());
        }

        return savedRecipe;
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
    private void saveRecipeIngredients(
            Recipe recipe,
            List<RecipeIngredientFormDto> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        for (RecipeIngredientFormDto ingredientDto : ingredients) {

            String ingredientName = ingredientDto.getName().trim();

            Ingredient ingredient = ingredientRepository
                    .findByNameIgnoreCase(ingredientName)
                    .orElseGet(() ->
                            ingredientRepository.save(new Ingredient(ingredientName))
                    );

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setAmount(ingredientDto.getAmount());
            recipeIngredient.setUnit(ingredientDto.getUnit().trim());
            recipeIngredient.setNotes(ingredientDto.getNotes());

            recipeIngredientRepository.save(recipeIngredient);
        }
    }
    private RecipeOutputDto toOutputDto(Recipe recipe) {

        List<RecipeIngredientOutputDto> ingredients = new ArrayList<>();

        List<RecipeIngredient> recipeIngredients =
                recipeIngredientRepository.findByRecipeId(recipe.getId());

        for (RecipeIngredient recipeIngredient : recipeIngredients) {

            RecipeIngredientOutputDto ingredientDto =
                    new RecipeIngredientOutputDto(
                            recipeIngredient.getId(),
                            recipeIngredient.getIngredient().getId(),
                            recipeIngredient.getIngredient().getName(),
                            recipeIngredient.getAmount(),
                            recipeIngredient.getUnit(),
                            recipeIngredient.getNotes()
                    );

            ingredients.add(ingredientDto);
        }

        return new RecipeOutputDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getPreparationTime(),
                recipe.getCookingTime(),
                recipe.getServings(),
                recipe.getImage(),
                recipe.getDifficulty(),
                recipe.getInstructions(),
                recipe.getCalories(),
                recipe.getProtein(),
                recipe.getCarbs(),
                recipe.getFat(),
                recipe.getCategories(),
                recipe.getOwner().getId(),
                recipe.getOwner().getUsername(),
                ingredients
        );
    }
    @Transactional(readOnly = true)
    public List<RecipeOutputDto> getAllRecipeOutputs() {
        return toOutputDtos(getAllRecipes());
    }

    @Transactional(readOnly = true)
    public List<RecipeOutputDto> getMyRecipeOutputs(String keycloakId) {
        return toOutputDtos(getMyRecipes(keycloakId));
    }

    @Transactional(readOnly = true)
    public List<RecipeOutputDto> getRecipeOutputsByCategory(Long categoryId) {
        return toOutputDtos(getRecipesByCategory(categoryId));
    }
    private void fillRecipe(Recipe recipe, RecipeInputDto recipeInputDto) {
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
    }
    private List<RecipeOutputDto> toOutputDtos(List<Recipe> recipes) {
        List<RecipeOutputDto> result = new ArrayList<>();

        for (Recipe recipe : recipes) {
            result.add(toOutputDto(recipe));
        }

        return result;
    }
}