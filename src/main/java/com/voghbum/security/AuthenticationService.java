package com.voghbum.security;

import com.voghbum.app.TenantContext;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.entity.User;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.db.repository.UserRepository;
import com.voghbum.dto.LoginRequest;
import com.voghbum.dto.LoginResponse;
import com.voghbum.dto.SignupRequest;
import com.voghbum.exception.AuthenticationException;
import com.voghbum.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final TenantRepository tenantRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationService(
            TenantRepository tenantRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            HttpServletRequest request,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService) {
        this.tenantRepository = tenantRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.request = request;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        String tenantId = request.getHeader("X-TenantID");
        if (tenantId == null || tenantId.isEmpty()) {
            throw new AuthenticationException("Tenant ID is required");
        }

        tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AuthenticationException("Invalid tenant ID"));

        try {
            if (userDetailsService.userExists(signupRequest.getUsername())) {
                throw new AuthenticationException("Username already exists");
            }

            userDetailsService.createUser(
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword())
            );
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String tenantId = request.getHeader("X-TenantID");
        if (tenantId == null || tenantId.isEmpty()) {
            throw new AuthenticationException("Tenant ID is required");
        }

        tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AuthenticationException("Invalid tenant ID"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails, tenantId);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(userDetails.getUsername());

            return response;
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }
}