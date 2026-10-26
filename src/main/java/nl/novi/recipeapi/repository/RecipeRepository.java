package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByCategories_Id(Long categoryId);

}