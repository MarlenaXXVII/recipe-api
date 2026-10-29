package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.WeekMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WeekMenuItemRepository extends JpaRepository<WeekMenuItem, Long> {

    List<WeekMenuItem> findByProfile_User_KeycloakIdOrderByDateAsc(
            String keycloakId
    );

    boolean existsByProfile_IdAndDateAndMealType(
            Long profileId,
            LocalDate date,
            nl.novi.recipeapi.model.MealType mealType
    );
}