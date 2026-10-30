package nl.novi.recipeapi.dto;

public class RecipeIngredientOutputDto {

    private Long id;
    private Long ingredientId;
    private String name;
    private Double amount;
    private String unit;
    private String notes;

    public RecipeIngredientOutputDto(
            Long id,
            Long ingredientId,
            String name,
            Double amount,
            String unit,
            String notes) {

        this.id = id;
        this.ingredientId = ingredientId;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.notes = notes;
    }

    public Long getId() {
        return id;
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

    public String getNotes() {
        return notes;
    }
}