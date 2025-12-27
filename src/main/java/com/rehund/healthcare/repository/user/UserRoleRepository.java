package com.rehund.healthcare.repository.user;

import com.rehund.healthcare.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    void deleteByIdUserId(Long userId);

    @Query(value = """
        SELECT * FROM user_roles ur
        WHERE ur.user_id = :userId AND ur.role_id = :roleId
    """, nativeQuery = true)
    Optional<UserRole> findByIdUserIdAndIdRoleId(Long userId, Long roleId);
}
