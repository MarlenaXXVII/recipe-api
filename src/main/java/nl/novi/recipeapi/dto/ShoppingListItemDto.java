package nl.novi.recipeapi.dto;

public class ShoppingListItemDto {

    private final Long ingredientId;
    private final String name;
    private final Double amount;
    private final String unit;

    public ShoppingListItemDto(
            Long ingredientId,
            String name,
            Double amount,
            String unit) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }
}