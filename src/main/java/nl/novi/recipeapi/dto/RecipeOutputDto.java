package nl.novi.recipeapi.dto;

import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.model.Difficulty;

import java.util.List;
import java.util.Set;

public class RecipeOutputDto {

    private final Long id;
    private final String title;
    private final String description;
    private final Integer preparationTime;
    private final Integer cookingTime;
    private final Integer servings;
    private final String image;
    private final Difficulty difficulty;
    private final String instructions;
    private final Integer calories;
    private final Double protein;
    private final Double carbs;
    private final Double fat;
    private final Set<Category> categories;
    private final Long ownerId;
    private final String ownerUsername;
    private final List<RecipeIngredientOutputDto> ingredients;

    public RecipeOutputDto(
            Long id,
            String title,
            String description,
            Integer preparationTime,
            Integer cookingTime,
            Integer servings,
            String image,
            Difficulty difficulty,
            String instructions,
            Integer calories,
            Double protein,
            Double carbs,
            Double fat,
            Set<Category> categories,
            Long ownerId,
            String ownerUsername,
            List<RecipeIngredientOutputDto> ingredients) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.image = image;
        this.difficulty = difficulty;
        this.instructions = instructions;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.categories = categories;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public Integer getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getInstructions() {
        return instructions;
    }

    public Integer getCalories() {
        return calories;
    }

    public Double getProtein() {
        return protein;
    }

    public Double getCarbs() {
        return carbs;
    }

    public Double getFat() {
        return fat;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public List<RecipeIngredientOutputDto> getIngredients() {
        return ingredients;
    }
}