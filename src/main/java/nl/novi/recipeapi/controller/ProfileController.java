package nl.novi.recipeapi.controller;

import jakarta.validation.Valid;
import nl.novi.recipeapi.dto.ProfileInputDto;
import nl.novi.recipeapi.dto.ProfileOutputDto;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileOutputDto> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        ProfileOutputDto profile = profileService.getProfileByKeycloakId(
                jwt.getSubject()
        );

        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<ProfileOutputDto> createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileInputDto inputDto) {

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        Profile profile = profileService.createProfile(
                keycloakId,
                email,
                inputDto.getUsername()
        );

        ProfileOutputDto outputDto = profileService.convertToOutputDto(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(outputDto);
    }
}
