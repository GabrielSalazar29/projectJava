package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.dto.AuthRequestDTO;
import com.agendacompromissos.V1.dto.AuthResponseDTO;
import com.agendacompromissos.V1.security.jwt.JwtUtil;
import com.agendacompromissos.V1.service.CustomUserDetailsService; // Seu UserDetailsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService; // Seu UserDetailsService

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) throws Exception {
        try {
            // Autentica o usuário com o AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Usuário ou senha inválidos", e);
        }

        // Se a autenticação for bem-sucedida, carrega UserDetails
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Gera o token JWT
        final String jwt = jwtUtil.generateToken(userDetails);

        // Retorna o token na resposta
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }
}