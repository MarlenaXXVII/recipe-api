package nl.novi.recipeapi.dto;

public class ShoppingListItemDto {

    private Long ingredientId;
    private String name;
    private Double amount;
    private String unit;

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