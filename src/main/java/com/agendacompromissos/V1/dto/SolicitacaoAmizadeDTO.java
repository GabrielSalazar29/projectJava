package com.agendacompromissos.V1.dto;

import com.agendacompromissos.V1.model.enums.StatusAmizade;
import java.time.LocalDateTime;

public class SolicitacaoAmizadeDTO {
    private Long id; // ID da entidade Amizade
    private UsuarioSummaryDTO solicitante;
    private UsuarioSummaryDTO solicitado;
    private StatusAmizade status;
    private LocalDateTime dataSolicitacao;

    // Construtor, Getters e Setters
    public SolicitacaoAmizadeDTO(Long id, UsuarioSummaryDTO solicitante, UsuarioSummaryDTO solicitado, StatusAmizade status, LocalDateTime dataSolicitacao) {
        this.id = id;
        this.solicitante = solicitante;
        this.solicitado = solicitado;
        this.status = status;
        this.dataSolicitacao = dataSolicitacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UsuarioSummaryDTO getSolicitante() { return solicitante; }
    public void setSolicitante(UsuarioSummaryDTO solicitante) { this.solicitante = solicitante; }
    public UsuarioSummaryDTO getSolicitado() { return solicitado; }
    public void setSolicitado(UsuarioSummaryDTO solicitado) { this.solicitado = solicitado; }
    public StatusAmizade getStatus() { return status; }
    public void setStatus(StatusAmizade status) { this.status = status; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
}
