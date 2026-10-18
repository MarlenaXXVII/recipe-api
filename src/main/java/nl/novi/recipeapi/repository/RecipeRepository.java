package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}