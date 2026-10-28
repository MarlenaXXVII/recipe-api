package nl.novi.recipeapi.controller;

import jakarta.validation.Valid;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.service.RecipeService;
import nl.novi.recipeapi.dto.RecipeInputDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

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
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(required = false) Long categoryId) {

        if (categoryId != null) {
            return ResponseEntity.ok(
                    recipeService.getRecipesByCategory(categoryId)
            );
        }

        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
    @GetMapping("/myrecipes")
    public ResponseEntity<List<Recipe>> getMyRecipes(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                recipeService.getMyRecipes(jwt.getSubject())
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeInputDto recipeInputDto) {

        Recipe savedRecipe = recipeService.createRecipe(
                recipeInputDto,
                jwt.getSubject()
        );

        URI location = URI.create("/recipes/" + savedRecipe.getId());

        return ResponseEntity.created(location).body(savedRecipe);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeInputDto recipeInputDto,
            @Valid @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        Recipe updatedRecipe = recipeService.updateRecipe(
                id,
                recipeInputDto,
                jwt.getSubject(),
                isAdmin
        );

        return ResponseEntity.ok(updatedRecipe);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        recipeService.deleteRecipe(id, jwt.getSubject(), isAdmin);

        return ResponseEntity.noContent().build();
    }
}