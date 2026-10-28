package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser("keycloak-123", "test@example.com");
    }

    @Test
    void findOrCreateUserReturnsExistingUser() {
        when(appUserRepository.findByKeycloakId("keycloak-123"))
                .thenReturn(Optional.of(appUser));

        AppUser result = appUserService.findOrCreateUser(
                "keycloak-123",
                "test@example.com"
        );

        assertSame(appUser, result);
        verify(appUserRepository).findByKeycloakId("keycloak-123");
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void findOrCreateUserCreatesNewUser() {
        when(appUserRepository.findByKeycloakId("keycloak-123"))
                .thenReturn(Optional.empty());

        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.findOrCreateUser(
                "keycloak-123",
                "test@example.com"
        );

        assertEquals("keycloak-123", result.getKeycloakId());
        assertEquals("test@example.com", result.getEmail());

        verify(appUserRepository).findByKeycloakId("keycloak-123");
        verify(appUserRepository).save(any(AppUser.class));
    }
}