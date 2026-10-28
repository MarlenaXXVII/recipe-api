package nl.novi.recipeapi.service;

import nl.novi.recipeapi.dto.ProfileOutputDto;
import nl.novi.recipeapi.exception.ProfileAlreadyExistsException;
import nl.novi.recipeapi.exception.ProfileNotFoundException;
import nl.novi.recipeapi.exception.UsernameAlreadyExistsException;
import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.model.Profile;
import nl.novi.recipeapi.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final AppUserService appUserService;

    public ProfileService(ProfileRepository profileRepository,
                          AppUserService appUserService) {
        this.profileRepository = profileRepository;
        this.appUserService = appUserService;
    }

    @Transactional
    public Profile createProfile(String keycloakId, String email, String username) {
        AppUser user = appUserService.findOrCreateUser(keycloakId, email);

        if (profileRepository.findByUser_KeycloakId(keycloakId).isPresent()) {
            throw new ProfileAlreadyExistsException("User already has a profile");
        }

        if (profileRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Profile profile = new Profile(username, user);

        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public ProfileOutputDto getProfileByKeycloakId(String keycloakId) {
        Profile profile = profileRepository.findByUser_KeycloakId(keycloakId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile does not exist"));

        return convertToOutputDto(profile);
    }

    public ProfileOutputDto convertToOutputDto(Profile profile) {
        return new ProfileOutputDto(
                profile.getId(),
                profile.getUsername(),
                profile.getUser().getEmail()
        );
    }

}
