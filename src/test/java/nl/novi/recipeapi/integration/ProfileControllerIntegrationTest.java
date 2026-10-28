package nl.novi.recipeapi.integration;

import tools.jackson.databind.ObjectMapper;
import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.repository.AppUserRepository;
import nl.novi.recipeapi.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    void getMyProfileWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/profiles/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfileReturnsProfile() throws Exception {
        AppUser user = appUserRepository.save(
                new AppUser("keycloak-123", "test@example.com")
        );

        profileRepository.save(
                new Profile("Kookliefhebber", user)
        );

        mockMvc.perform(get("/profiles/me")
                        .with(jwt()
                                .jwt(token -> token.subject("keycloak-123"))
                                .authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Kookliefhebber"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createProfileReturnsCreated() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                Map.of("username", "NieuweKok")
        );

        mockMvc.perform(post("/profiles")
                        .with(jwt()
                                .jwt(token -> token
                                        .subject("keycloak-456")
                                        .claim("email", "new@example.com"))
                                .authorities(() -> "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("NieuweKok"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void createProfileWithInvalidUsernameReturnsBadRequest() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                Map.of("username", "ab")
        );

        mockMvc.perform(post("/profiles")
                        .with(jwt()
                                .jwt(token -> token
                                        .subject("keycloak-789")
                                        .claim("email", "invalid@example.com"))
                                .authorities(() -> "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
