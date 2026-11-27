package com.gabgym.api.controllers;

import com.gabgym.api.entities.UserExperiencePreferences;
import com.gabgym.api.repositories.UserExperiencePreferencesRepository;
import com.gabgym.api.security.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;

@RestController
@RequestMapping("/api/users/me/preferences")
public class ExperiencePreferencesController {
    private static final Set<String> MODES=Set.of("simple","normal","complete");
    private static final Set<String> LANGUAGES=Set.of("pt","en","es","fr");
    private final AuthorizationService authorization;
    private final UserExperiencePreferencesRepository preferences;

    public ExperiencePreferencesController(AuthorizationService authorization,
            UserExperiencePreferencesRepository preferences) {
        this.authorization=authorization;
        this.preferences=preferences;
    }

    public record Preferences(String mode, Boolean food, Boolean workout, Boolean weight,
                              Boolean coach, String language, Boolean completed) {
        public static Preferences from(UserExperiencePreferences state) {
            return new Preferences(state.getMode(),state.isFood(),state.isWorkout(),
                state.isWeight(),state.isCoach(),state.getLanguage(),state.isCompleted());
        }
    }

    @GetMapping
    public Preferences getMine(){
        Long id=authorization.currentUser().getId();
        return Preferences.from(preferences.findById(id).orElseGet(UserExperiencePreferences::new));
    }

    @PutMapping
    @Transactional
    public Preferences updateMine(@RequestBody Preferences input) {
        Long id=authorization.currentUser().getId();
        if(input==null || !MODES.contains(input.mode()) || !LANGUAGES.contains(input.language())
                || input.food()==null || input.workout()==null || input.weight()==null
                || input.coach()==null || input.completed()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preferências inválidas");
        }
        UserExperiencePreferences current=preferences.findById(id).orElseGet(()->{
            UserExperiencePreferences created=new UserExperiencePreferences();
            created.setUserId(id);
            return created;
        });
        current.setMode(input.mode());
        current.setLanguage(input.language());
        current.setFood(input.food());
        current.setWorkout(input.workout());
        current.setWeight(input.weight());
        current.setCoach(input.coach());
        current.setCompleted(input.completed());
        return Preferences.from(preferences.save(current));
    }
}
