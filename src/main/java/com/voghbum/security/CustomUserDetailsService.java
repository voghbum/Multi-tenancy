package com.voghbum.security;

import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final TenantRepository tenantRepository;

    public CustomUserDetailsService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In a real application, you would have a separate User entity and repository
        // For this example, we'll use the tenant ID as the username
        Tenant tenant = tenantRepository.findByTenantId(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tenant not found: " + username));

        return new User(
                tenant.getTenantId(),
                tenant.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + tenant.getTenantId()))
        );
    }
} 