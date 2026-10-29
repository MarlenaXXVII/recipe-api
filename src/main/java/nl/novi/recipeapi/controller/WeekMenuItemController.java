package nl.novi.recipeapi.controller;

import jakarta.validation.Valid;
import nl.novi.recipeapi.dto.WeekMenuItemInputDto;
import nl.novi.recipeapi.dto.WeekMenuItemOutputDto;
import nl.novi.recipeapi.service.WeekMenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/weekmenu")
public class WeekMenuItemController {

    private final WeekMenuItemService weekMenuItemService;

    public WeekMenuItemController(WeekMenuItemService weekMenuItemService) {
        this.weekMenuItemService = weekMenuItemService;
    }

    @GetMapping
    public ResponseEntity<List<WeekMenuItemOutputDto>> getMyWeekMenu(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                weekMenuItemService.getMyWeekMenu(jwt.getSubject())
        );
    }

    @PostMapping
    public ResponseEntity<WeekMenuItemOutputDto> createWeekMenuItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody WeekMenuItemInputDto inputDto) {

        WeekMenuItemOutputDto result = weekMenuItemService.createWeekMenuItem(
                inputDto,
                jwt.getSubject()
        );

        return ResponseEntity
                .created(URI.create("/weekmenu/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeekMenuItemOutputDto> updateWeekMenuItem(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody WeekMenuItemInputDto inputDto) {
        return ResponseEntity.ok(
                weekMenuItemService.updateWeekMenuItem(
                        id,
                        inputDto,
                        jwt.getSubject()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeekMenuItem(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        weekMenuItemService.deleteWeekMenuItem(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}