package com.rehund.healthcare.repository.user;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleType roleName);

    @Query(value = """
        SELECT r.* FROM roles r
        JOIN user_roles ur ON r.role_id = ur.role_id
        JOIN users u ON ur.user_id = u.user_id
        WHERE u.user_id = :userId
        """ , nativeQuery = true )
    List<Role> findByUserId(Long userId);
}
