package nl.novi.recipeapi.controller;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.service.RecipeService;
import org.springframework.web.bind.annotation.*;
import nl.novi.recipeapi.dto.RecipeInputDto;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeInputDto recipeInputDto) {

        Recipe savedRecipe = recipeService.createRecipe(recipeInputDto);

        URI location = URI.create("/recipes/" + savedRecipe.getId());

        return ResponseEntity
                .created(location)
                .body(savedRecipe);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeInputDto recipeInputDto) {

        Recipe updatedRecipe = recipeService.updateRecipe(id, recipeInputDto);

        return ResponseEntity.ok(updatedRecipe);
    }
}