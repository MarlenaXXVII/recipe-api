package nl.novi.recipeapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecipeIngredientFormDto {

    @NotBlank(message = "Please enter an ingredient name.")
    private String name;

    @NotNull(message = "Please enter an amount.")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Amount must be greater than zero.")
    private Double amount;

    @NotBlank(message = "Please enter a unit.")
    private String unit;

    private String notes;

    public RecipeIngredientFormDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}