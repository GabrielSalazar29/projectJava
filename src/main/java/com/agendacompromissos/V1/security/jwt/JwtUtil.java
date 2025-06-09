package com.agendacompromissos.V1.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Importe este
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationInMs;

    private Key secretKey;

    @PostConstruct
    public void init() {
        if (secretString == null || secretString.trim().isEmpty()) {
            logger.error("!!! ERRO CRÍTICO: jwt.secret NÃO está configurado ou está vazio. !!!");
            throw new IllegalArgumentException("A propriedade jwt.secret não pode ser nula ou vazia.");
        }
        logger.info("Chave secreta JWT carregada (tamanho: {} caracteres).", secretString.length());
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.warn("Falha ao extrair username do token. Causa: {}", e.getMessage());
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("Tentando fazer parse e validar assinatura do token...");
        try {
            Claims claims = Jwts.parser()
                   .setSigningKey(secretKey) // Define a chave para verificar a assinatura
                   .build()
                   .parseClaimsJws(token) // Faz o parse e verifica o token
                   .getPayload(); // Retorna o corpo (claims)
            logger.debug("Parse e validação de assinatura do token bem-sucedidos.");
            return claims;
        } catch (SignatureException e) {
            logger.error("<<<<< ERRO DE ASSINATURA JWT! A chave secreta está incorreta ou o token foi adulterado. >>>>>", e);
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("<<<<< ERRO: Token JWT malformado. >>>>>", e);
            throw e;
        } catch (ExpiredJwtException e) {
            logger.warn("<<<<< AVISO: Token JWT expirou. >>>>>", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("<<<<< ERRO: Token JWT não suportado. >>>>>", e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("<<<<< ERRO: String de claims JWT está vazia ou nula. >>>>>", e);
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            boolean isExpired = extractExpiration(token).before(new Date());
            if (isExpired) {
                logger.warn("Verificação de expiração: O token está expirado.");
            } else {
                logger.debug("Verificação de expiração: O token é válido.");
            }
            return isExpired;
        } catch (Exception e) {
            // Se a extração falhou (ex: assinatura inválida), já consideramos o token inválido.
            return true;
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        logger.info("Gerando novo token para '{}' com expiração em {}", subject, expiryDate);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Iniciando validação completa do token para o usuário '{}'.", userDetails.getUsername());
        final String username = extractUsername(token);
        if (username == null) {
            logger.warn("Validação falhou: não foi possível extrair o username do token.");
            return false;
        }
        boolean isUsernameMatch = username.equals(userDetails.getUsername());
        if (!isUsernameMatch) {
            logger.warn("Validação falhou: o username do token ('{}') não corresponde ao username do UserDetails ('{}').", username, userDetails.getUsername());
        }
        boolean isExpired = isTokenExpired(token); // isTokenExpired já loga seu status
        
        boolean isValid = isUsernameMatch && !isExpired;
        logger.debug("Resultado da validação final para '{}': {}", username, isValid ? "VÁLIDO" : "INVÁLIDO");
        return isValid;
    }
}
