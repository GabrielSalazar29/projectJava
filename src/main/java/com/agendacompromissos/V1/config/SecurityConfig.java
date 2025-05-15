package com.agendacompromissos.V1.config;

import com.agendacompromissos.V1.security.jwt.JwtRequestFilter;
import com.agendacompromissos.V1.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler; // Para tratar 403
import org.springframework.security.web.AuthenticationEntryPoint; // Para tratar 401
import jakarta.servlet.http.HttpServletResponse; // Para os handlers
import java.util.Date;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List; 

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 1. Desabilitar CSRF (comum para APIs stateless)
            .csrf(csrf -> csrf.disable())
            // 2. Configurar a política de criação de sessão para STATELESS
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 3. Configurar regras de autorização para os endpoints
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Endpoint de login
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll() // Endpoint de registro    
                .requestMatchers("/api/compromissos/**").authenticated() // Proteger endpoints de compromissos
                .anyRequest().authenticated() // Qualquer outra requisição precisa de autenticação
            )
            // 4. Adicionar o filtro JWT antes do filtro de UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            // 5. Configurar Exception Handling para erros de autenticação e autorização
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint()) // Lida com falhas de autenticação (401)
                .accessDeniedHandler(accessDeniedHandler())       // Lida com falhas de autorização (403)
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // Para console H2

        return http.build();
    }

     @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173"
        ));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Cabeçalhos permitidos na requisição
        // "Authorization" é crucial para JWT. "Content-Type" é comum.
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração para todos os paths sob /api/
        // Ajuste o padrão do path conforme necessário (ex: "/**" para todos os paths)
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // Retorna 401 Unauthorized para tentativas de acesso não autenticadas a recursos protegidos
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"timestamp\": \"" + new Date() + "\","
                                     + "\"status\": 401,"
                                     + "\"error\": \"Unauthorized\","
                                     + "\"message\": \"" + authException.getMessage() + "\","
                                     + "\"path\": \"" + request.getRequestURI() + "\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        // Retorna 403 Forbidden se um usuário autenticado tenta acessar um recurso que não tem permissão
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"timestamp\": \"" + new Date() + "\","
                                     + "\"status\": 403,"
                                     + "\"error\": \"Forbidden\","
                                     + "\"message\": \"" + accessDeniedException.getMessage() + "\","
                                     + "\"path\": \"" + request.getRequestURI() + "\"}");
        };
    }
}