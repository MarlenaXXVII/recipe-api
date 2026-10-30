package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    List<RecipeIngredient> findByRecipeId(Long recipeId);

    List<RecipeIngredient> findByRecipe_IdIn(Collection<Long> recipeIds);

    void deleteByRecipe_Id(Long recipeId);
}