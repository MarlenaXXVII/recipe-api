package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.ProfileOutputDto;
import nl.novi.recipeapi.exception.ProfileAlreadyExistsException;
import nl.novi.recipeapi.exception.ProfileNotFoundException;
import nl.novi.recipeapi.exception.UsernameAlreadyExistsException;
import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private ProfileService profileService;

    private AppUser appUser;
    private Profile profile;

    @BeforeEach
    void setUp() {
        appUser = new AppUser("keycloak-123", "test@example.com");
        profile = new Profile("Kookliefhebber", appUser);
    }

    @Test
    void createProfileSuccessfully() {
        when(appUserService.findOrCreateUser("keycloak-123", "test@example.com"))
                .thenReturn(appUser);

        when(profileRepository.findByUser_KeycloakId("keycloak-123"))
                .thenReturn(Optional.empty());

        when(profileRepository.findByUsername("Kookliefhebber"))
                .thenReturn(Optional.empty());

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Profile result = profileService.createProfile(
                "keycloak-123",
                "test@example.com",
                "Kookliefhebber"
        );

        assertEquals("Kookliefhebber", result.getUsername());
        assertSame(appUser, result.getUser());

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void createProfileThrowsExceptionWhenProfileAlreadyExists() {
        when(appUserService.findOrCreateUser("keycloak-123", "test@example.com"))
                .thenReturn(appUser);

        when(profileRepository.findByUser_KeycloakId("keycloak-123"))
                .thenReturn(Optional.of(profile));

        assertThrows(ProfileAlreadyExistsException.class, () ->
                profileService.createProfile(
                        "keycloak-123",
                        "test@example.com",
                        "Kookliefhebber"
                )
        );

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void createProfileThrowsExceptionWhenUsernameAlreadyExists() {
        when(appUserService.findOrCreateUser("keycloak-123", "test@example.com"))
                .thenReturn(appUser);

        when(profileRepository.findByUser_KeycloakId("keycloak-123"))
                .thenReturn(Optional.empty());

        when(profileRepository.findByUsername("Kookliefhebber"))
                .thenReturn(Optional.of(profile));

        assertThrows(UsernameAlreadyExistsException.class, () ->
                profileService.createProfile(
                        "keycloak-123",
                        "test@example.com",
                        "Kookliefhebber"
                )
        );

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void getProfileByKeycloakIdReturnsProfile() {
        when(profileRepository.findByUser_KeycloakId("keycloak-123"))
                .thenReturn(Optional.of(profile));

        ProfileOutputDto result = profileService.getProfileByKeycloakId(
                "keycloak-123"
        );

        assertEquals("Kookliefhebber", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getProfileByKeycloakIdThrowsExceptionWhenNotFound() {
        when(profileRepository.findByUser_KeycloakId("keycloak-123"))
                .thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () ->
                profileService.getProfileByKeycloakId("keycloak-123")
        );
    }

    @Test
    void convertToOutputDtoReturnsCorrectData() {
        ProfileOutputDto result = profileService.convertToOutputDto(profile);

        assertEquals("Kookliefhebber", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }
}
