package nl.novi.recipeapi.dto;

import nl.novi.recipeapi.model.MealType;

import java.time.LocalDate;

public class WeekMenuItemOutputDto {

    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private LocalDate date;
    private MealType mealType;

    public WeekMenuItemOutputDto() {
    }

    public WeekMenuItemOutputDto(
            Long id,
            Long recipeId,
            String recipeTitle,
            LocalDate date,
            MealType mealType) {
        this.id = id;
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.date = date;
        this.mealType = mealType;
    }

    public Long getId() {
        return id;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public LocalDate getDate() {
        return date;
    }

    public MealType getMealType() {
        return mealType;
    }
}