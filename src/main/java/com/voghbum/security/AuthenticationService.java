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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public AuthenticationService(UserRepository userRepository,
                               TenantRepository tenantRepository,
                               PasswordEncoder passwordEncoder,
                               JwtService jwtService,
                               HttpServletRequest request) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.request = request;
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        String tenantId = request.getHeader("X-TenantID");
        if (tenantId == null || tenantId.isEmpty()) {
            throw new AuthenticationException("Tenant ID is required");
        }

        // Önce master veritabanında tenant'ın varlığını kontrol et
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AuthenticationException("Invalid tenant ID"));

        // Tenant'ın veritabanına bağlan
        TenantContext.setCurrentTenant(tenantId);

        try {
            // Kullanıcı adının benzersiz olup olmadığını kontrol et
            if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
                throw new AuthenticationException("Username already exists");
            }

            // Yeni kullanıcı oluştur
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

            // Kullanıcıyı kaydet
            userRepository.save(user);
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String tenantId = request.getHeader("X-TenantID");
        if (tenantId == null || tenantId.isEmpty()) {
            throw new AuthenticationException("Tenant ID is required");
        }

        // Önce master veritabanında tenant'ın varlığını kontrol et
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AuthenticationException("Invalid tenant ID"));

        // Tenant'ın veritabanına bağlan
        TenantContext.setCurrentTenant(tenantId);

        try {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new AuthenticationException("Invalid username or password");
            }

            String token = jwtService.generateToken(user, tenantId);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());

            return response;
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }
}