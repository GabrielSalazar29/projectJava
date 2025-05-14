package com.agendacompromissos.V1.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // Para geração de chaves seguras
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // Importação correta para @PostConstruct
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    // Chave Secreta:
    // Esta chave é USADA PARA ASSINAR e VERIFICAR os tokens JWT.
    // DEVE ser uma string longa, complexa e mantida em segredo.
    // NÃO a coloque diretamente no código em produção. Use application.properties,
    // variáveis de ambiente ou um sistema de gerenciamento de segredos.
    // Para HS256, a chave deve ter pelo menos 256 bits (32 bytes).
    // Exemplo de como gerar no terminal: openssl rand -hex 32
    @Value("${jwt.secret}")
    private String secretString;

    // Tempo de Expiração do Token (em milissegundos):
    // Define por quanto tempo o token será válido.
    // Ex: 3600000 (1 hora), 86400000 (24 horas)
    @Value("${jwt.expiration.ms}")
    private long jwtExpirationInMs;

    private Key secretKey; // Objeto Key do java.security para a chave

    /**
     * Método executado após a injeção de dependências para inicializar a secretKey.
     * Converte a string da chave secreta (lida do application.properties)
     * em um objeto Key utilizável pela biblioteca jjwt.
     */
    @PostConstruct
    public void init() {
        // Garante que a chave secreta tenha um tamanho adequado para o algoritmo (ex: HS256)
        // Keys.hmacShaKeyFor espera bytes.
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * Extrai o nome de usuário (subject) do token JWT.
     *
     * @param token O token JWT.
     * @return O nome de usuário.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiração do token JWT.
     *
     * @param token O token JWT.
     * @return A data de expiração.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Função genérica para extrair uma informação específica (claim) do token.
     *
     * @param token O token JWT.
     * @param claimsResolver Função que define qual claim extrair.
     * @return O valor da claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todas as claims (informações) do corpo do token JWT.
     * Para isso, ele verifica a assinatura do token usando a secretKey.
     * Se a assinatura for inválida ou o token estiver malformado, uma exceção será lançada.
     *
     * @param token O token JWT.
     * @return Um objeto Claims contendo todas as informações do token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey) // Define a chave para verificar a assinatura
                   .build()
                   .parseClaimsJws(token) // Faz o parse e verifica o token
                   .getPayload(); // Retorna o corpo (claims)
    }

    /**
     * Verifica se o token JWT expirou.
     *
     * @param token O token JWT.
     * @return true se o token expirou, false caso contrário.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Gera um novo token JWT para o usuário fornecido.
     *
     * @param userDetails Detalhes do usuário (geralmente obtidos do UserDetailsService).
     * @return O token JWT como uma String.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Você pode adicionar claims personalizadas aqui se precisar.
        // Por exemplo, se você reintroduzir roles:
        // List<String> roles = userDetails.getAuthorities().stream()
        // .map(GrantedAuthority::getAuthority)
        // .collect(Collectors.toList());
        // claims.put("roles", roles);

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Método auxiliar para criar o token com as claims e o subject (nome de usuário).
     * Define a data de emissão, data de expiração e assina o token com a secretKey.
     *
     * @param claims Claims personalizadas a serem adicionadas ao token.
     * @param subject O "assunto" do token, geralmente o nome de usuário.
     * @return O token JWT compactado como String.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                . claims(claims) // Adiciona as claims personalizadas
                .subject(subject) // Define o "assunto" (usuário)
                .issuedAt(now) // Data de emissão
                .expiration(expiryDate) // Data de expiração
                .signWith(secretKey, SignatureAlgorithm.HS256) // Assina com a chave e algoritmo
                // Se sua chave for para HS384 ou HS512, mude o SignatureAlgorithm aqui e
                // garanta que a chave em `secretString` tenha o tamanho adequado.
                // Ex: SignatureAlgorithm.HS512
                .compact(); // Constrói e compacta o token
    }

    /**
     * Valida o token JWT.
     * Verifica se o nome de usuário no token corresponde ao UserDetails fornecido
     * e se o token não expirou.
     *
     * @param token O token JWT.
     * @param userDetails Os detalhes do usuário para comparação.
     * @return true se o token for válido, false caso contrário.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String usernameInToken = extractUsername(token);
        return (usernameInToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
