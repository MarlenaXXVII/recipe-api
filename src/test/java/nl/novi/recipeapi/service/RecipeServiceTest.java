
package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.RecipeInputDto;
import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.model.Category;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.model.Recipe;
import nl.novi.recipeapi.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    private static final String USER_ID = "keycloak-user-123";
    private static final String OTHER_USER_ID = "keycloak-user-456";

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    private Profile createProfile(String keycloakId) {
        AppUser user = new AppUser(keycloakId, keycloakId + "@example.com");
        return new Profile("user-" + keycloakId, user);
    }

    private Recipe createOwnedRecipe(String keycloakId) {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta Carbonara");
        recipe.setOwner(createProfile(keycloakId));
        return recipe;
    }

    private RecipeInputDto createInputDto() {
        RecipeInputDto inputDto = new RecipeInputDto();
        inputDto.setTitle("Vegetarische Pasta");
        inputDto.setDescription("Pasta met groenten");
        inputDto.setPreparationTime(15);
        inputDto.setCookingTime(20);
        inputDto.setServings(4);
        inputDto.setInstructions("Kook de pasta.");
        return inputDto;
    }

    @Test
    void getAllRecipesReturnsAllRecipes() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Pasta Carbonara");

        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getAllRecipes();

        assertEquals(1, result.size());
        assertEquals("Pasta Carbonara", result.get(0).getTitle());
    }

    @Test
    void createRecipeSavesRecipeWithCorrectOwner() {
        RecipeInputDto inputDto = createInputDto();
        Profile profile = createProfile(USER_ID);

        when(profileRepository.findByUser_KeycloakId(USER_ID))
                .thenReturn(Optional.of(profile));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> {
                    Recipe recipe = invocation.getArgument(0);
                    recipe.setId(1L);
                    return recipe;
                });

        Recipe result = recipeService.createRecipe(inputDto, USER_ID);

        assertEquals(1L, result.getId());
        assertEquals("Vegetarische Pasta", result.getTitle());
        assertSame(profile, result.getOwner());

        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void createRecipeThrowsExceptionWhenProfileNotFound() {
        RecipeInputDto inputDto = createInputDto();

        when(profileRepository.findByUser_KeycloakId(USER_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.createRecipe(inputDto, USER_ID)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void getRecipeByIdReturnsRecipe() {
        Recipe recipe = createOwnedRecipe(USER_ID);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getRecipeById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Pasta Carbonara", result.getTitle());
    }

    @Test
    void getRecipeByIdThrowsExceptionWhenNotFound() {
        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getRecipeById(999L)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void getMyRecipesReturnsOnlyOwnRecipes() {
        Recipe recipe = createOwnedRecipe(USER_ID);

        when(recipeRepository.findByOwner_User_KeycloakId(USER_ID))
                .thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getMyRecipes(USER_ID);

        assertEquals(1, result.size());
        assertEquals(USER_ID,
                result.get(0).getOwner().getUser().getKeycloakId());

        verify(recipeRepository).findByOwner_User_KeycloakId(USER_ID);
    }

    @Test
    void getMyRecipesReturnsEmptyList() {
        when(recipeRepository.findByOwner_User_KeycloakId(USER_ID))
                .thenReturn(List.of());

        List<Recipe> result = recipeService.getMyRecipes(USER_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateRecipeUpdatesOwnRecipe() {
        Recipe existingRecipe = createOwnedRecipe(USER_ID);
        RecipeInputDto inputDto = createInputDto();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(existingRecipe));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.updateRecipe(
                1L, inputDto, USER_ID, false
        );

        assertEquals("Vegetarische Pasta", result.getTitle());
        assertEquals("Pasta met groenten", result.getDescription());
        assertEquals(4, result.getServings());

        verify(recipeRepository).save(existingRecipe);
    }

    @Test
    void updateRecipeThrowsForbiddenForOtherUser() {
        Recipe recipe = createOwnedRecipe(OTHER_USER_ID);
        RecipeInputDto inputDto = createInputDto();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.updateRecipe(
                        1L, inputDto, USER_ID, false
                )
        );

        assertEquals(403, exception.getStatusCode().value());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void adminCanUpdateOtherUsersRecipe() {
        Recipe recipe = createOwnedRecipe(OTHER_USER_ID);
        RecipeInputDto inputDto = createInputDto();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.updateRecipe(
                1L, inputDto, USER_ID, true
        );

        assertEquals("Vegetarische Pasta", result.getTitle());
        assertEquals(OTHER_USER_ID,
                result.getOwner().getUser().getKeycloakId());

        verify(recipeRepository).save(recipe);
    }

    @Test
    void updateRecipeThrowsNotFound() {
        RecipeInputDto inputDto = createInputDto();

        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.updateRecipe(
                        999L, inputDto, USER_ID, false
                )
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void deleteRecipeDeletesOwnRecipe() {
        Recipe recipe = createOwnedRecipe(USER_ID);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(1L, USER_ID, false);

        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipeThrowsForbiddenForOtherUser() {
        Recipe recipe = createOwnedRecipe(OTHER_USER_ID);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(
                        1L, USER_ID, false
                )
        );

        assertEquals(403, exception.getStatusCode().value());
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void adminCanDeleteOtherUsersRecipe() {
        Recipe recipe = createOwnedRecipe(OTHER_USER_ID);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(1L, USER_ID, true);

        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipeThrowsNotFound() {
        when(recipeRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(
                        999L, USER_ID, true
                )
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void createRecipeWithCategoriesSavesCategories() {
        RecipeInputDto inputDto = createInputDto();
        inputDto.setCategoryIds(Set.of(1L, 2L));

        Profile profile = createProfile(USER_ID);

        Category dinner = new Category();
        dinner.setId(1L);
        dinner.setName("Diner");

        Category lunch = new Category();
        lunch.setId(2L);
        lunch.setName("Lunch");

        when(profileRepository.findByUser_KeycloakId(USER_ID))
                .thenReturn(Optional.of(profile));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(dinner));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(lunch));

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(inputDto, USER_ID);

        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains(dinner));
        assertTrue(result.getCategories().contains(lunch));
    }

    @Test
    void createRecipeThrowsExceptionWhenCategoryNotFound() {
        RecipeInputDto inputDto = createInputDto();
        inputDto.setCategoryIds(Set.of(999L));

        when(profileRepository.findByUser_KeycloakId(USER_ID))
                .thenReturn(Optional.of(createProfile(USER_ID)));

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.createRecipe(inputDto, USER_ID)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void getRecipesByCategoryReturnsRecipes() {
        Recipe recipe = createOwnedRecipe(USER_ID);

        when(categoryRepository.existsById(2L))
                .thenReturn(true);

        when(recipeRepository.findByCategories_Id(2L))
                .thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getRecipesByCategory(2L);

        assertEquals(1, result.size());
        assertEquals("Pasta Carbonara", result.get(0).getTitle());
    }

    @Test
    void getRecipesByCategoryThrowsExceptionWhenCategoryNotFound() {
        when(categoryRepository.existsById(999L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getRecipesByCategory(999L)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(recipeRepository, never()).findByCategories_Id(999L);
    }

    @Test
    void getRecipesByCategoryReturnsEmptyList() {
        when(categoryRepository.existsById(3L))
                .thenReturn(true);

        when(recipeRepository.findByCategories_Id(3L))
                .thenReturn(List.of());

        List<Recipe> result = recipeService.getRecipesByCategory(3L);

        assertTrue(result.isEmpty());
    }
}
