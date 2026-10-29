package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.WeekMenuItemInputDto;
import nl.novi.recipeapi.dto.WeekMenuItemOutputDto;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.model.WeekMenuItem;
import nl.novi.recipeapi.repository.ProfileRepository;
import nl.novi.recipeapi.repository.RecipeRepository;
import nl.novi.recipeapi.repository.WeekMenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WeekMenuItemService {

    private final WeekMenuItemRepository weekMenuItemRepository;
    private final ProfileRepository profileRepository;
    private final RecipeRepository recipeRepository;

    public WeekMenuItemService(
            WeekMenuItemRepository weekMenuItemRepository,
            ProfileRepository profileRepository,
            RecipeRepository recipeRepository) {
        this.weekMenuItemRepository = weekMenuItemRepository;
        this.profileRepository = profileRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional(readOnly = true)
    public List<WeekMenuItemOutputDto> getMyWeekMenu(String keycloakId) {
        return weekMenuItemRepository
                .findByProfile_User_KeycloakIdOrderByDateAsc(keycloakId)
                .stream()
                .map(this::toOutputDto)
                .toList();
    }

    @Transactional
    public WeekMenuItemOutputDto createWeekMenuItem(
            WeekMenuItemInputDto inputDto,
            String keycloakId) {

        Profile profile = getProfile(keycloakId);
        Recipe recipe = getRecipe(inputDto.getRecipeId());

        if (weekMenuItemRepository.existsByProfile_IdAndDateAndMealType(
                profile.getId(),
                inputDto.getDate(),
                inputDto.getMealType())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have a meal planned for this time."
            );
        }

        WeekMenuItem item = new WeekMenuItem();
        item.setProfile(profile);
        item.setRecipe(recipe);
        item.setDate(inputDto.getDate());
        item.setMealType(inputDto.getMealType());

        return toOutputDto(weekMenuItemRepository.save(item));
    }

    @Transactional
    public WeekMenuItemOutputDto updateWeekMenuItem(
            Long id,
            WeekMenuItemInputDto inputDto,
            String keycloakId) {

        WeekMenuItem item = getOwnedItem(id, keycloakId);
        Recipe recipe = getRecipe(inputDto.getRecipeId());

        boolean slotChanged =
                !item.getDate().equals(inputDto.getDate())
                        || item.getMealType() != inputDto.getMealType();

        if (slotChanged && weekMenuItemRepository.existsByProfile_IdAndDateAndMealType(
                item.getProfile().getId(),
                inputDto.getDate(),
                inputDto.getMealType())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have a meal planned for this time."
            );
        }

        item.setRecipe(recipe);
        item.setDate(inputDto.getDate());
        item.setMealType(inputDto.getMealType());

        return toOutputDto(weekMenuItemRepository.save(item));
    }

    @Transactional
    public void deleteWeekMenuItem(Long id, String keycloakId) {
        WeekMenuItem item = getOwnedItem(id, keycloakId);
        weekMenuItemRepository.delete(item);
    }

    private Profile getProfile(String keycloakId) {
        return profileRepository.findByUser_KeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "We couldn't find your profile."
                ));
    }

    private Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "We couldn't find that recipe."
                ));
    }

    private WeekMenuItem getOwnedItem(Long id, String keycloakId) {
        WeekMenuItem item = weekMenuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "We couldn't find that planned meal."
                ));

        if (!item.getProfile().getUser().getKeycloakId().equals(keycloakId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only change your own week menu."
            );
        }

        return item;
    }

    private WeekMenuItemOutputDto toOutputDto(WeekMenuItem item) {
        return new WeekMenuItemOutputDto(
                item.getId(),
                item.getRecipe().getId(),
                item.getRecipe().getTitle(),
                item.getDate(),
                item.getMealType()
        );
    }
}