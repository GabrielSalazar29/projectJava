package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.dto.UsuarioSummaryDTO;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.repository.UsuarioRepository;
import com.agendacompromissos.V1.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DTO (Data Transfer Object) para o registro de um novo usuário.
 * Usado para receber os dados da requisição de forma segura, sem expor
 * diretamente a entidade `Usuario`.
 */
class RegistroUsuarioDTO {
    private String username;
    private String password;

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

@RestController
@RequestMapping("/api/usuarios") // Endpoint base para as operações de usuário
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para a busca

    /**
     * Endpoint para registrar um novo usuário no sistema.
     * Recebe os dados do usuário através do corpo da requisição (JSON).
     *
     * @param registroDTO Dados para o registro do novo usuário (username e password).
     * @return ResponseEntity com o usuário criado (sem a senha) e status HTTP 201 (Created),
     * ou uma mensagem de erro com status HTTP 400 (Bad Request) em caso de falha.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroUsuarioDTO registroDTO) {
        try {
            // Cria uma nova instância da entidade Usuario
            // O construtor Usuario() ou Usuario(username, password) pode ser usado.
            // Usaremos o construtor padrão e setters aqui para clareza.
            Usuario novoUsuario = new Usuario();
            novoUsuario.setUsername(registroDTO.getUsername());
            novoUsuario.setPassword(registroDTO.getPassword()); // A senha será hasheada no UsuarioService

            // Chama o serviço para registrar o novo usuário.
            // O UsuarioService agora lida com o hash da senha e salva o usuário sem roles.
            Usuario usuarioSalvo = usuarioService.registrarNovoUsuario(novoUsuario);

            // É uma boa prática não retornar a senha (mesmo hasheada) na resposta da API.
            UsuarioSummaryDTO usuarioResponse = new UsuarioSummaryDTO(usuarioSalvo.getId(), usuarioSalvo.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
        } catch (Exception e) {
            // Em caso de erro (ex: nome de usuário já existe), retorna uma mensagem de erro.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

     // NOVO ENDPOINT PARA BUSCAR USUÁRIOS
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioSummaryDTO>> buscarUsuarios(
            @RequestParam("termo") String termo,
            @AuthenticationPrincipal Usuario currentUser) {
        if (termo == null || termo.trim().length() < 2) { // Não busca por termos muito curtos
            return ResponseEntity.badRequest().body(List.of());
        }
        // Busca usuários cujo nome contém o termo, excluindo o próprio usuário logado
        List<Usuario> usuariosEncontrados = usuarioRepository.findByUsernameContainingIgnoreCaseAndIdNot(termo.trim(), currentUser.getId());
        
        List<UsuarioSummaryDTO> dtos = usuariosEncontrados.stream()
                .map(u -> new UsuarioSummaryDTO(u.getId(), u.getUsername()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
