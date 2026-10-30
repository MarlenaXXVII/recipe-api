package nl.novi.recipeapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import nl.novi.recipeapi.model.Difficulty;

import java.util.List;
import java.util.Set;

public class RecipeInputDto {

    @NotBlank(message = "Please enter a recipe title.")
    @Size(max = 255, message = "Your recipe title is too long.")
    private String title;

    @NotBlank(message = "Please enter a recipe description.")
    @Size(max = 2000, message = "Your description is too long.")
    private String description;

    @NotNull(message = "Please enter a preparation time.")
    @Min(value = 1, message = "Preparation time must be at least 1 minute.")
    private Integer preparationTime;

    @NotNull(message = "Please enter a cooking time.")
    @Min(value = 1, message = "Cooking time must be at least 1 minute.")
    private Integer cookingTime;

    @NotNull(message = "Please enter the number of servings.")
    @Min(value = 1, message = "Please enter at least 1 serving.")
    private Integer servings;

    private String image;

    @NotNull(message = "Please select a difficulty.")
    private Difficulty difficulty;

    @NotBlank(message = "Please add the cooking instructions.")
    private String instructions;

    @Min(value = 0, message = "Calories can't be negative.")
    private Integer calories;

    @DecimalMin(value = "0.0", message = "Protein can't be negative.")
    private Double protein;

    @DecimalMin(value = "0.0", message = "Carbs can't be negative.")
    private Double carbs;

    @DecimalMin(value = "0.0", message = "Fat can't be negative.")
    private Double fat;

    private Set<Long> categoryIds;

    @Valid
    @NotEmpty(message = "Please add at least one ingredient.")
    private List<RecipeIngredientFormDto> ingredients;


    public RecipeInputDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<RecipeIngredientFormDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientFormDto> ingredients) {
        this.ingredients = ingredients;
    }

}