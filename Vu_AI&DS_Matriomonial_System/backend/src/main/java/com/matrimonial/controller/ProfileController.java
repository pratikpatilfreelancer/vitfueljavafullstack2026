package com.matrimonial.controller;

import com.matrimonial.dto.ProfileRequest;
import com.matrimonial.entity.Profile;
import com.matrimonial.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FIX BUG-06: Now uses ProfileRequest DTO for create/update endpoints
 * instead of accepting the raw Profile JPA entity (mass-assignment protection).
 * @Valid enforces server-side validation on all inbound profile data.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Profile> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequest request) {
        return new ResponseEntity<>(profileService.createProfile(userId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Profile>> searchProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer age) {
        return ResponseEntity.ok(profileService.searchProfiles(gender, city, age));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Profile> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<Profile> getProfileById(@PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getProfileById(profileId));
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(profileId, request));
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable Long profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }
}
