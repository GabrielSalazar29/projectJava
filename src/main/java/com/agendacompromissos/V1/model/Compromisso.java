package com.agendacompromissos.V1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; 
import java.time.Instant;

@Entity
@Table(name = "compromissos")

public class Compromisso {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gerador de ID autoincremental
    private Long id;

    private String titulo;
    private String descricao;
    private Instant  dataHoraInicio; // Use LocalDateTime para data e hora
    private Instant  dataHoraFim;
    private String local;

    @ManyToOne(fetch = FetchType.LAZY) // Muitos compromissos para UM usuário. LAZY é bom para performance.
    @JoinColumn(name = "usuario_id", nullable = false) // Define a coluna da chave estrangeira e que não pode ser nula
    private Usuario usuario; // Referência ao proprietário do compromisso

    public Compromisso() {
    }

    public Compromisso(String titulo, String descricao, Instant  dataHoraInicio, Instant  dataHoraFim, String local, Usuario usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.local = local;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Instant  getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Instant  dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Instant  getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Instant  dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // toString, equals, hashCode (opcional, mas bom para debugging)
}