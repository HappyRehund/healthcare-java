package com.rehund.healthcare.repository.user;

import com.rehund.healthcare.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    void deleteByIdUserId(Long userId);
}
