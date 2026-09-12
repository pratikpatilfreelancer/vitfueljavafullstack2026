package com.matrimonial.dto;

public class AuthResponse {

    private Long userId;
    private String name;
    private String email;
    private boolean hasProfile;
    private Long profileId;

    public AuthResponse() {
    }

    public AuthResponse(Long userId, String name, String email, boolean hasProfile, Long profileId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.hasProfile = hasProfile;
        this.profileId = profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHasProfile() {
        return hasProfile;
    }

    public void setHasProfile(boolean hasProfile) {
        this.hasProfile = hasProfile;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
}
