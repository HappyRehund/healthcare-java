package com.rehund.healthcare.service.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.repository.user.RoleRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.rehund.healthcare.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CacheService cacheService;

    private final String USER_CACHE_KEY = "cache:user:";
    private final String USER_ROLES_CACHE_KEY = "cache:user:roles:";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String userCacheKey = USER_CACHE_KEY + username;
        String userRolesCacheKey = USER_ROLES_CACHE_KEY + username;

        Optional<User> userOpt = cacheService.get(userCacheKey, User.class);
        Optional<List<Role>> rolesOpt = cacheService.get(userRolesCacheKey,
                new TypeReference<>() {
                });

        if (userOpt.isPresent() && rolesOpt.isPresent()) {
            log.info("User and roles found in cache for username: {}", username);
            return UserInfo
                    .builder()
                    .user(userOpt.get())
                    .roles(rolesOpt.get())
                    .build();

        } else {

            log.info("User or roles not found in cache for username: {}. Fetching from database.", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            List<Role> roles = roleRepository.findByUserId(user.getUserId());

            // Store in cache
            cacheService.put(userCacheKey, user);
            cacheService.put(userRolesCacheKey, roles);

            return UserInfo
                    .builder()
                    .user(user)
                    .roles(roles)
                    .build();
        }
    }
}
