package com.mediaflow.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mediaflow.api.dto.UserAuth;
import com.mediaflow.api.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(
            value = """
                    SELECT 
    u.user_id AS id,
    u.name AS userName,
    u.email AS email,
    u.date_birth AS dateBirth,
    u.password_hash AS passwordHash,
    ARRAY_AGG(r.name) AS roles,
    p.profile_id AS "profileId",
    p.display_name AS "displayName",
    p.preferred_language AS "preferredLanguage",
    p.avatar_url AS "avatarUrl",
    p.bio AS "bio"
FROM users u 
LEFT JOIN users_roles ur ON u.user_id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.role_id 
LEFT JOIN profiles p ON u.user_id = p.user_id 
WHERE u.email = :email  
GROUP BY 
    u.user_id,
    u.name, 
    u.email, 
    u.date_birth, 
    u.password_hash,
    p.profile_id, 
    p.display_name,
    p.preferred_language,
    p.avatar_url,
    p.bio;
                    """,
            nativeQuery = true
    )
    Optional<UserAuth> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailForAuth(@Param("email") String email);
}
