package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.model.Usuario; // Importe Usuario
import com.agendacompromissos.V1.service.CompromissoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para injetar o usuário logado
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Para exceções HTTP
import java.time.LocalDateTime;
import java.util.List;

// DTO para receber dados de criação/atualização de compromisso
// Não inclui o usuário, pois será pego do contexto de segurança
class CompromissoDTO {
    private String descricao;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String local;

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    // Método para converter DTO para Entidade (sem o usuário)
    public Compromisso toEntity() {
        Compromisso compromisso = new Compromisso();
        compromisso.setDescricao(this.descricao);
        compromisso.setDataHoraInicio(this.dataHoraInicio);
        compromisso.setDataHoraFim(this.dataHoraFim);
        compromisso.setLocal(this.local);
        return compromisso;
    }
}


@RestController
@RequestMapping("/api/compromissos")
public class CompromissoController {

    private final CompromissoService compromissoService;

    @Autowired
    public CompromissoController(CompromissoService compromissoService) {
        this.compromissoService = compromissoService;
    }

    private Long getUsuarioIdAutenticado(Usuario usuarioAutenticado) {
        if (usuarioAutenticado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return usuarioAutenticado.getId();
    }

    @GetMapping
    public ResponseEntity<List<Compromisso>> listarCompromissosDoUsuario(
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long usuarioId = getUsuarioIdAutenticado(usuarioAutenticado);
        List<Compromisso> compromissos = compromissoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(compromissos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compromisso> buscarCompromissoPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long usuarioId = getUsuarioIdAutenticado(usuarioAutenticado);
        return compromissoService.buscarPorIdEUsuarioId(id, usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Compromisso> criarCompromisso(
            @RequestBody CompromissoDTO compromissoDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long usuarioId = getUsuarioIdAutenticado(usuarioAutenticado);
        try {
            Compromisso novoCompromisso = compromissoDTO.toEntity();
            Compromisso compromissoSalvo = compromissoService.salvar(novoCompromisso, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(compromissoSalvo);
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compromisso> atualizarCompromisso(
            @PathVariable Long id,
            @RequestBody CompromissoDTO compromissoDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long usuarioId = getUsuarioIdAutenticado(usuarioAutenticado);
        try {
            Compromisso compromissoAtualizado = compromissoDTO.toEntity();
            Compromisso salvo = compromissoService.atualizar(id, compromissoAtualizado, usuarioId);
            return ResponseEntity.ok(salvo);
        } catch (RuntimeException e) { // Pode ser mais específico (ex: CompromissoNaoEncontradoException)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompromisso(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long usuarioId = getUsuarioIdAutenticado(usuarioAutenticado);
        try {
            compromissoService.deletarPorIdEUsuarioId(id, usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Pode ser mais específico
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}