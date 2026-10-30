package nl.novi.recipeapi.controller;

import jakarta.validation.Valid;
import nl.novi.recipeapi.dto.RecipeOutputDto;
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
    public ResponseEntity<List<RecipeOutputDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipeOutputs());
    }
    @GetMapping("/myrecipes")
    public ResponseEntity<List<RecipeOutputDto>> getMyRecipes(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                recipeService.getMyRecipeOutputs(jwt.getSubject())
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<RecipeOutputDto> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeOutputById(id));
    }
    @PostMapping
    public ResponseEntity<RecipeOutputDto> createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeInputDto recipeInputDto) {

        Recipe savedRecipe = recipeService.createRecipe(
                recipeInputDto,
                jwt.getSubject()
        );

        URI location = URI.create("/recipes/" + savedRecipe.getId());

        return ResponseEntity.created(location)
                .body(recipeService.getRecipeOutputById(savedRecipe.getId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeInputDto recipeInputDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

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
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        recipeService.deleteRecipe(id, jwt.getSubject(), isAdmin);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RecipeOutputDto>> getRecipesByCategory(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                recipeService.getRecipeOutputsByCategory(categoryId)
        );
    }
}