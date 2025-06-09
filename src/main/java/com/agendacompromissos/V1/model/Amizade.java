package com.agendacompromissos.V1.model;

import com.agendacompromissos.V1.model.enums.StatusAmizade;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "amizades",
       uniqueConstraints = {
           // Garante que só exista uma solicitação entre os mesmos dois usuários
           @UniqueConstraint(columnNames = {"solicitante_id", "solicitado_id"})
       })
public class Amizade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante; // Quem enviou o pedido

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_id", nullable = false)
    private Usuario solicitado; // Quem recebeu o pedido

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAmizade status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacaoStatus;

    public Amizade() {
    }

    public Amizade(Usuario solicitante, Usuario solicitado) {
        this.solicitante = solicitante;
        this.solicitado = solicitado;
        this.status = StatusAmizade.PENDENTE;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getSolicitado() {
        return solicitado;
    }

    public void setSolicitado(Usuario solicitado) {
        this.solicitado = solicitado;
    }

    public StatusAmizade getStatus() {
        return status;
    }

    public void setStatus(StatusAmizade status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataAtualizacaoStatus() {
        return dataAtualizacaoStatus;
    }

    public void setDataAtualizacaoStatus(LocalDateTime dataAtualizacaoStatus) {
        this.dataAtualizacaoStatus = dataAtualizacaoStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amizade amizade = (Amizade) o;
        return id != null && id.equals(amizade.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}