package com.agendacompromissos.V1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime; // Importe LocalDateTime

@Entity // Anotação para indicar que esta classe é uma entidade JPA

public class Compromisso {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gerador de ID autoincremental
    private Long id;

    private String descricao;
    private LocalDateTime dataHoraInicio; // Use LocalDateTime para data e hora
    private LocalDateTime dataHoraFim;
    private String local;

    public Compromisso() {
    }

    public Compromisso(String descricao, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String local) {
        this.descricao = descricao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.local = local;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    // toString, equals, hashCode (opcional, mas bom para debugging)
}