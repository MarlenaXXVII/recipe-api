package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.ShoppingListItemDto;
import nl.novi.recipeapi.model.RecipeIngredient;
import nl.novi.recipeapi.model.WeekMenuItem;
import nl.novi.recipeapi.repository.RecipeIngredientRepository;
import nl.novi.recipeapi.repository.WeekMenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {

    private final WeekMenuItemRepository weekMenuItemRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public ShoppingListService(
            WeekMenuItemRepository weekMenuItemRepository,
            RecipeIngredientRepository recipeIngredientRepository) {
        this.weekMenuItemRepository = weekMenuItemRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @Transactional(readOnly = true)
    public List<ShoppingListItemDto> getShoppingList(
            String keycloakId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be before or equal to end date."
            );
        }

        List<WeekMenuItem> weekMenuItems =
                weekMenuItemRepository
                        .findByProfile_User_KeycloakIdAndDateBetweenOrderByDateAsc(
                                keycloakId, startDate, endDate
                        );

        if (weekMenuItems.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> recipeCounts = weekMenuItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRecipe().getId(),
                        Collectors.counting()
                ));

        Set<Long> recipeIds = recipeCounts.keySet();

        List<RecipeIngredient> recipeIngredients =
                recipeIngredientRepository.findByRecipe_IdIn(recipeIds);

        Map<String, ShoppingListItemDto> shoppingList = new LinkedHashMap<>();

        for (RecipeIngredient recipeIngredient : recipeIngredients) {

            Long ingredientId = recipeIngredient.getIngredient().getId();
            String name = recipeIngredient.getIngredient().getName();
            String unit = recipeIngredient.getUnit();

            double amount = recipeIngredient.getAmount()
                    * recipeCounts.get(recipeIngredient.getRecipe().getId());

            String key = ingredientId + "|" + unit;

            if (shoppingList.containsKey(key)) {
                ShoppingListItemDto existing = shoppingList.get(key);

                shoppingList.put(
                        key,
                        new ShoppingListItemDto(
                                ingredientId,
                                name,
                                existing.getAmount() + amount,
                                unit
                        )
                );
            } else {
                shoppingList.put(
                        key,
                        new ShoppingListItemDto(
                                ingredientId,
                                name,
                                amount,
                                unit
                        )
                );
            }
        }

        return new ArrayList<>(shoppingList.values());
    }
}