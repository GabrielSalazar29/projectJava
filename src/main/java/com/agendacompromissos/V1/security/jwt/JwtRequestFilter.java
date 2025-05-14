package com.agendacompromissos.V1.security.jwt;

import com.agendacompromissos.V1.service.CustomUserDetailsService; // Seu UserDetailsService
import io.jsonwebtoken.ExpiredJwtException; // Para tratar token expirado
import io.jsonwebtoken.MalformedJwtException; // Para tratar token malformado
import io.jsonwebtoken.SignatureException; // Para tratar falha na assinatura (obsoleto em jjwt 0.12, use io.jsonwebtoken.security.SignatureException)
import io.jsonwebtoken.UnsupportedJwtException; // Para tratar token não suportado
import io.jsonwebtoken.security.SecurityException; // Importação correta para exceções de segurança do jjwt > 0.12.x
                                                   // Se estiver usando jjwt <= 0.11.x, io.jsonwebtoken.SignatureException é comum.

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger; // Para logging
import org.slf4j.LoggerFactory; // Para logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // Para verificar strings
import org.springframework.web.filter.OncePerRequestFilter; // Garante que o filtro seja executado apenas uma vez por requisição

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter { // Herda de OncePerRequestFilter

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Este método é o núcleo do filtro. Ele é chamado para cada requisição.
     * 1. Extrai o token JWT do cabeçalho Authorization.
     * 2. Valida o token.
     * 3. Se válido, carrega os detalhes do usuário e configura a autenticação no SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        String username = null;
        String jwt = null;

        // Verifica se o cabeçalho Authorization existe e se começa com "Bearer "
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            jwt = authorizationHeader.substring(BEARER_PREFIX.length()); // Extrai o token (removendo "Bearer ")
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                logger.error("Não foi possível obter o nome de usuário do token JWT: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.warn("Token JWT expirado: {}", e.getMessage());
                // Você pode querer que o AuthenticationEntryPoint lide com isso,
                // ou retornar uma resposta de erro específica aqui se preferir.
                // request.setAttribute("expired", e.getMessage()); // Exemplo para passar info adiante
            } catch (UnsupportedJwtException e) {
                logger.error("Token JWT não suportado: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                logger.error("Token JWT inválido (malformado): {}", e.getMessage());
            } catch (SecurityException e) { // Para jjwt > 0.12.x (substitui SignatureException em muitos casos)
                 logger.error("Falha na assinatura do JWT: {}", e.getMessage());
            }

        } else {
            // Se não houver token, apenas loga (pode ser uma requisição para um endpoint público)
            if (authorizationHeader != null) {
                logger.warn("Token JWT não começa com 'Bearer ' String");
            }
        }

        // Se o username foi extraído do token e não há autenticação no contexto de segurança atual
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carrega os detalhes do usuário a partir do username extraído
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Valida o token (verifica se o username corresponde e se não está expirado)
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Se o token for válido, cria um objeto de autenticação
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // authorities vêm do UserDetails

                // Define os detalhes da autenticação (ex: endereço IP, etc.)
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define o objeto de autenticação no SecurityContextHolder.
                // A partir deste ponto, o usuário é considerado autenticado para esta requisição.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.debug("Usuário '{}' autenticado com sucesso via JWT.", username);
            } else {
                logger.warn("Token JWT inválido para o usuário '{}'.", username);
            }
        }

        // Continua a cadeia de filtros. Se a autenticação foi configurada acima,
        // os próximos filtros e o DispatcherServlet saberão que o usuário está autenticado.
        chain.doFilter(request, response);
    }
}
