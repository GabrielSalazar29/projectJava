package com.agendacompromissos.V1.security.jwt;

import com.agendacompromissos.V1.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        logger.info(">>> Iniciando JwtRequestFilter para a requisição: {} {}", request.getMethod(), request.getRequestURI());

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            logger.debug("Cabeçalho 'Authorization' encontrado. Extraindo JWT.");
            username = jwtUtil.extractUsername(jwt);
            if (username != null) {
                logger.debug("Username extraído do token: '{}'", username);
            } else {
                logger.warn("O username extraído do token é nulo. O token pode ser inválido ou expirado.");
            }
        } else {
            logger.debug("Cabeçalho 'Authorization' não encontrado ou não começa com 'Bearer '.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Username '{}' presente e não há autenticação no SecurityContext. Prosseguindo com a validação.", username);
            UserDetails userDetails = null;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.debug("UserDetails para '{}' carregado com sucesso do banco de dados.", username);
            } catch (UsernameNotFoundException e) {
                logger.warn("Usuário '{}' não encontrado no banco de dados.", username);
            }

            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails)) {
                logger.info("<<< SUCESSO! Token JWT validado para o usuário: '{}'. Definindo autenticação no SecurityContext. >>>", username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.warn("<<< FALHA NA VALIDAÇÃO! O token JWT para o usuário '{}' não é válido. SecurityContext não será definido. >>>", username);
            }
        } else {
            if (username == null) {
                logger.debug("Nenhum username no token. Ignorando validação (pode ser uma rota pública).");
            } else {
                logger.debug("Já existe uma autenticação no SecurityContext. O filtro JWT não fará nada.");
            }
        }
        
        logger.info(">>> Finalizando JwtRequestFilter. Passando para o próximo filtro na cadeia.");
        chain.doFilter(request, response);
    }
}