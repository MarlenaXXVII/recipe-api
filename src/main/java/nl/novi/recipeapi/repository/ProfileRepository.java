package nl.novi.recipeapi.repository;

import nl.novi.recipeapi.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUsername(String username);

    Optional<Profile> findByUser_KeycloakId(String keycloakId);
}
