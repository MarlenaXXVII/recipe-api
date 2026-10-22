package nl.novi.recipeapi.controller;

import nl.novi.recipeapi.dto.IngredientInputDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(
            @RequestBody IngredientInputDto inputDto) {

        Ingredient savedIngredient = ingredientService.createIngredient(inputDto);

        return ResponseEntity
                .created(URI.create("/ingredients/" + savedIngredient.getId()))
                .body(savedIngredient);
    }
}
