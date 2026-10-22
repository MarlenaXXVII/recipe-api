package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
