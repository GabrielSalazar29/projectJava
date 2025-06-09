package com.agendacompromissos.V1.controller;
import com.agendacompromissos.V1.dto.AuthRequestDTO;
import com.agendacompromissos.V1.dto.AuthResponseDTO; // Importe o novo DTO
import com.agendacompromissos.V1.dto.UsuarioSummaryDTO; // Importe o DTO de resumo
import com.agendacompromissos.V1.model.Usuario; // Importe a sua entidade Usuario
import com.agendacompromissos.V1.security.jwt.JwtUtil;
import com.agendacompromissos.V1.service.CustomUserDetailsService;
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
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) throws Exception {
        try {
            // Autentica o utilizador com o AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Se as credenciais forem inválidas, lança uma exceção que será tratada
            throw new Exception("Utilizador ou senha inválidos", e);
        }

        // Se a autenticação for bem-sucedida, carrega UserDetails para gerar o token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // MUDANÇA: Como o nosso UserDetails é a própria entidade Usuario, podemos fazer o cast
        // para obter o objeto completo e o seu ID.
        if (!(userDetails instanceof Usuario)) {
             throw new Exception("Erro interno: UserDetails não é uma instância de Usuario.");
        }
        final Usuario usuarioCompleto = (Usuario) userDetails;

        // Gera o token JWT
        final String jwt = jwtUtil.generateToken(userDetails);
        
        // MUDANÇA: Cria o DTO de resumo do utilizador
        UsuarioSummaryDTO userDto = new UsuarioSummaryDTO(usuarioCompleto.getId(), usuarioCompleto.getUsername());
        
        // MUDANÇA: Retorna a nova classe de resposta que contém o token E os dados do utilizador
        return ResponseEntity.ok(new AuthResponseDTO(jwt, userDto));
    }
}