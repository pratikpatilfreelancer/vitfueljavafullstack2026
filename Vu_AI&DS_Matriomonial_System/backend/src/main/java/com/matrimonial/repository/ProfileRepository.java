package com.matrimonial.repository;

import com.matrimonial.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserUserId(Long userId);

    @Query("SELECT p FROM Profile p JOIN p.user u WHERE " +
           "(:gender IS NULL OR :gender = '' OR LOWER(p.gender) = LOWER(:gender)) AND " +
           "(:city IS NULL OR :city = '' OR LOWER(p.city) = LOWER(:city)) AND " +
           "(:age IS NULL OR p.age = :age)")
    List<Profile> searchProfiles(@Param("gender") String gender,
                                 @Param("city") String city,
                                 @Param("age") Integer age);

    @Modifying
    @Query(value = "DELETE FROM profiles WHERE profile_id = :profileId", nativeQuery = true)
    void deleteProfileById(@Param("profileId") Long profileId);
}
