package com.agendacompromissos.V1.dto;

import java.time.Instant;
import java.util.List;

public class CompromissoCreateDTO {
    private String titulo;
    private String descricao;
    private Instant dataHoraInicio;
    private Instant dataHoraFim;
    private String local;
    private List<Long> amigoIds; // IDs dos amigos convidados

    // Getters e Setters para todos os campos
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Instant getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Instant dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public Instant getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Instant dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public List<Long> getAmigoIds() { return amigoIds; }
    public void setAmigoIds(List<Long> amigoIds) { this.amigoIds = amigoIds; }
}