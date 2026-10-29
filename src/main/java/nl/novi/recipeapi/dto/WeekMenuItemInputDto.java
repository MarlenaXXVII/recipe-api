package nl.novi.recipeapi.dto;

import jakarta.validation.constraints.NotNull;
import nl.novi.recipeapi.model.MealType;

import java.time.LocalDate;

public class WeekMenuItemInputDto {

    @NotNull(message = "Please select a recipe.")
    private Long recipeId;

    @NotNull(message = "Please select a date.")
    private LocalDate date;

    @NotNull(message = "Please select a meal type.")
    private MealType mealType;

    public WeekMenuItemInputDto() {
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
}