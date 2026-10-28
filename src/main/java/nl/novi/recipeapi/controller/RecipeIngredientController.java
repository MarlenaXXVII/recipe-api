package nl.novi.recipeapi.controller;

import nl.novi.recipeapi.dto.RecipeIngredientInputDto;
import nl.novi.recipeapi.model.RecipeIngredient;
import nl.novi.recipeapi.service.RecipeIngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/recipes/{recipeId}/ingredients")
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;

    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    @PostMapping
    public ResponseEntity<RecipeIngredient> addIngredientToRecipe(
            @PathVariable Long recipeId,
            @RequestBody RecipeIngredientInputDto inputDto) {

        RecipeIngredient savedRecipeIngredient =
                recipeIngredientService.addIngredientToRecipe(recipeId, inputDto);

        return ResponseEntity
                .created(URI.create("/recipes/" + recipeId
                        + "/ingredients/" + savedRecipeIngredient.getId()))
                .body(savedRecipeIngredient);
    }

    @GetMapping
    public ResponseEntity<List<RecipeIngredient>> getIngredientsByRecipeId(
            @PathVariable Long recipeId) {

        return ResponseEntity.ok(
                recipeIngredientService.getIngredientsByRecipeId(recipeId)
        );
    }
}
