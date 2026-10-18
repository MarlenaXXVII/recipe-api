package nl.novi.recipeapi.controller;

import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import nl.novi.recipeapi.dto.RecipeInputDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeInputDto recipeInputDto) {

        Recipe savedRecipe = recipeService.createRecipe(recipeInputDto);

        URI location = URI.create("/recipes/" + savedRecipe.getId());

        return ResponseEntity
                .created(location)
                .body(savedRecipe);
    }
}