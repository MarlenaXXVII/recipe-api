package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.IngredientInputDto;
import nl.novi.recipeapi.model.Ingredient;
import nl.novi.recipeapi.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient createIngredient(IngredientInputDto inputDto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(inputDto.getName());

        return ingredientRepository.save(ingredient);
    }

    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ingredient not found with id: " + id
                ));
    }
}
