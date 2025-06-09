package com.agendacompromissos.V1.controller;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.model.Usuario; // Importe Usuario
import com.agendacompromissos.V1.service.CompromissoService;

import jakarta.validation.Valid;

import com.agendacompromissos.V1.dto.CompromissoCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para injetar o usuário logado
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Para exceções HTTP
import java.time.Instant;
import java.util.List;

// DTO para receber dados de criação/atualização de compromisso
// Não inclui o usuário, pois será pego do contexto de segurança
class CompromissoDTO {
    private String titulo;
    private String descricao;
    private Instant  dataHoraInicio;
    private Instant  dataHoraFim;
    private String local;

    // Getters e Setters
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Instant  getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Instant  dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public Instant  getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Instant  dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    // Método para converter DTO para Entidade (sem o usuário)
    public Compromisso toEntity() {
        Compromisso compromisso = new Compromisso();
        compromisso.setTitulo(this.titulo);
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
            @AuthenticationPrincipal Usuario currentUser) {
        // Este método agora retorna compromissos onde o usuário é um participante
        List<Compromisso> compromissos = compromissoService.listarPorUsuario(currentUser.getId());
        // TODO: Mapear para um DTO de resposta para evitar loops de serialização
        // e expor dados de forma controlada.
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
            @RequestBody CompromissoCreateDTO compromissoDTO, // Usa o novo DTO
            @AuthenticationPrincipal Usuario currentUser) {
        
        // A validação do DTO (com @Valid) seria ideal aqui
        Compromisso compromissoSalvo = compromissoService.salvarCompartilhado(compromissoDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(compromissoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compromisso> atualizarCompromisso(
            @PathVariable Long id,
            @Valid @RequestBody CompromissoCreateDTO compromissoDTO,
            @AuthenticationPrincipal Usuario currentUser) {
        
        Compromisso compromissoAtualizado = compromissoService.atualizar(id, compromissoDTO, currentUser.getId());
        return ResponseEntity.ok(compromissoAtualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompromisso(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario currentUser) {
            
        compromissoService.deletar(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}