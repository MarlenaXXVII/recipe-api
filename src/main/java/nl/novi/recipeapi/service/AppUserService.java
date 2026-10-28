package nl.novi.recipeapi.service;

import nl.novi.recipeapi.model.AppUser;
import nl.novi.recipeapi.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public AppUser findOrCreateUser(String keycloakId, String email) {
        return appUserRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    AppUser user = new AppUser(keycloakId, email);
                    return appUserRepository.save(user);
                });
    }
}