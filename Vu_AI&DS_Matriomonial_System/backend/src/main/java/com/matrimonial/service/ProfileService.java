package com.matrimonial.service;

import com.matrimonial.dto.ProfileRequest;
import com.matrimonial.entity.Profile;
import com.matrimonial.entity.User;
import com.matrimonial.exception.ProfileNotFoundException;
import com.matrimonial.repository.InterestRepository;
import com.matrimonial.repository.ProfileRepository;
import com.matrimonial.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, InterestRepository interestRepository, UserRepository userRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.interestRepository = interestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * FIX BUG-06: Now accepts ProfileRequest DTO instead of raw Profile entity.
     * Manually maps only safe user-submitted fields to the Profile entity.
     */
    public Profile createProfile(Long userId, ProfileRequest request) {
        User user = userService.getUserById(userId);
        if (profileRepository.findByUserUserId(userId).isPresent()) {
            throw new IllegalArgumentException("This user already has a profile");
        }
        Profile profile = mapRequestToProfile(request, new Profile());
        profile.setUser(user);
        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));
    }

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user ID: " + userId));
    }

    public List<Profile> searchProfiles(String gender, String city, Integer age) {
        return profileRepository.searchProfiles(emptyToNull(gender), emptyToNull(city), age);
    }

    /**
     * FIX BUG-06: Now accepts ProfileRequest DTO instead of raw Profile entity.
     */
    public Profile updateProfile(Long profileId, ProfileRequest request) {
        Profile existingProfile = getProfileById(profileId);
        mapRequestToProfile(request, existingProfile);
        return profileRepository.save(existingProfile);
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = getProfileById(profileId);
        Long userId = profile.getUser().getUserId();
        interestRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private Profile mapRequestToProfile(ProfileRequest request, Profile profile) {
        if (request.getAge() == null || request.getAge() < 18) {
            throw new IllegalArgumentException("Age must be 18 or above");
        }
        if (request.getGender() == null || request.getGender().isBlank()) {
            throw new IllegalArgumentException("Gender is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setCity(request.getCity());
        profile.setEducation(request.getEducation());
        profile.setOccupation(request.getOccupation());
        profile.setAbout(request.getAbout());
        profile.setPhotoUrl(request.getPhotoUrl());
        return profile;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
