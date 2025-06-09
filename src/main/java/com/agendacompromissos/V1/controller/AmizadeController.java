package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.dto.SolicitacaoAmizadeDTO;
import com.agendacompromissos.V1.dto.UsuarioSummaryDTO;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.service.AmizadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar todas as operações relacionadas a amizades entre usuários.
 * Todos os endpoints aqui são protegidos e requerem um usuário autenticado.
 */
@RestController
@RequestMapping("/api/amizades")
public class AmizadeController {

    private final AmizadeService amizadeService;

    @Autowired
    public AmizadeController(AmizadeService amizadeService) {
        this.amizadeService = amizadeService;
    }

    /**
     * Envia uma nova solicitação de amizade do usuário logado para outro usuário.
     * @param currentUser O usuário que está enviando a solicitação (autenticado por JWT).
     * @param solicitadoId O ID do usuário que receberá a solicitação.
     * @return Os detalhes da nova solicitação pendente.
     */
    @PostMapping("/solicitar/{solicitadoId}")
    public ResponseEntity<SolicitacaoAmizadeDTO> enviarSolicitacao(
            @AuthenticationPrincipal Usuario currentUser,
            @PathVariable Long solicitadoId) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SolicitacaoAmizadeDTO solicitacao = amizadeService.enviarSolicitacaoAmizade(currentUser.getId(), solicitadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacao);
    }

    /**
     * Aceita uma solicitação de amizade pendente que foi enviada ao usuário logado.
     * @param currentUser O usuário que está aceitando a solicitação.
     * @param amizadeId O ID da entidade Amizade (da solicitação pendente).
     * @return Os detalhes da solicitação agora com status ACEITO.
     */
    @PostMapping("/aceitar/{amizadeId}")
    public ResponseEntity<SolicitacaoAmizadeDTO> aceitarSolicitacao(
            @AuthenticationPrincipal Usuario currentUser,
            @PathVariable Long amizadeId) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        SolicitacaoAmizadeDTO solicitacao = amizadeService.aceitarSolicitacaoAmizade(amizadeId, currentUser.getId());
        return ResponseEntity.ok(solicitacao);
    }

    /**
     * Rejeita uma solicitação de amizade pendente que foi enviada ao usuário logado.
     * @param currentUser O usuário que está rejeitando a solicitação.
     * @param amizadeId O ID da entidade Amizade (da solicitação pendente).
     * @return Os detalhes da solicitação agora com status RECUSADO.
     */
    @PostMapping("/rejeitar/{amizadeId}")
    public ResponseEntity<SolicitacaoAmizadeDTO> rejeitarSolicitacao(
            @AuthenticationPrincipal Usuario currentUser,
            @PathVariable Long amizadeId) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        SolicitacaoAmizadeDTO solicitacao = amizadeService.rejeitarSolicitacaoAmizade(amizadeId, currentUser.getId());
        return ResponseEntity.ok(solicitacao);
    }

    /**
     * Desfaz uma amizade existente entre o usuário logado e outro usuário.
     * @param currentUser O usuário que está desfazendo a amizade.
     * @param amigoId O ID do usuário cuja amizade será desfeita.
     * @return Resposta sem conteúdo (204 No Content).
     */
    @DeleteMapping("/desfazer/{amigoId}")
    public ResponseEntity<Void> desfazerAmizade(
            @AuthenticationPrincipal Usuario currentUser,
            @PathVariable Long amigoId) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        amizadeService.desfazerAmizade(currentUser.getId(), amigoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os amigos do usuário logado (amizades com status ACEITO).
     * @param currentUser O usuário logado.
     * @return Uma lista de DTOs resumidos dos usuários amigos.
     */
    @GetMapping("/meus-amigos")
    public ResponseEntity<List<UsuarioSummaryDTO>> listarMeusAmigos(@AuthenticationPrincipal Usuario currentUser) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<UsuarioSummaryDTO> amigos = amizadeService.listarAmigos(currentUser.getId());
        return ResponseEntity.ok(amigos);
    }

    /**
     * Lista todas as solicitações de amizade pendentes recebidas pelo usuário logado.
     * @param currentUser O usuário logado.
     * @return Uma lista de DTOs das solicitações de amizade.
     */
    @GetMapping("/solicitacoes/recebidas")
    public ResponseEntity<List<SolicitacaoAmizadeDTO>> listarSolicitacoesRecebidas(@AuthenticationPrincipal Usuario currentUser) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<SolicitacaoAmizadeDTO> solicitacoes = amizadeService.listarSolicitacoesPendentesRecebidas(currentUser.getId());
        return ResponseEntity.ok(solicitacoes);
    }

    /**
     * Lista todas as solicitações de amizade pendentes enviadas pelo usuário logado.
     * @param currentUser O usuário logado.
     * @return Uma lista de DTOs das solicitações de amizade.
     */
    @GetMapping("/solicitacoes/enviadas")
    public ResponseEntity<List<SolicitacaoAmizadeDTO>> listarSolicitacoesEnviadas(@AuthenticationPrincipal Usuario currentUser) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<SolicitacaoAmizadeDTO> solicitacoes = amizadeService.listarSolicitacoesPendentesEnviadas(currentUser.getId());
        return ResponseEntity.ok(solicitacoes);
    }
}
