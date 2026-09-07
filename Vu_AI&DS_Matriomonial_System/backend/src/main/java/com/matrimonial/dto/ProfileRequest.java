package com.matrimonial.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * FIX BUG-06: ProfileController previously accepted the raw Profile entity,
 * enabling mass-assignment of any field including profileId and user reference.
 * This DTO restricts input to only user-submittable fields with full validation.
 */
public class ProfileRequest {

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be 18 or above")
    @Max(value = 99, message = "Age must be 99 or below")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Size(max = 200, message = "Education must be at most 200 characters")
    private String education;

    @Size(max = 200, message = "Occupation must be at most 200 characters")
    private String occupation;

    @Size(max = 1000, message = "About must be at most 1000 characters")
    private String about;

    @Size(max = 500, message = "Photo URL must be at most 500 characters")
    private String photoUrl;

    public ProfileRequest() {}

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
