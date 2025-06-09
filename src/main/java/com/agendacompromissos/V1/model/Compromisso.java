package com.agendacompromissos.V1.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compromissos")
public class Compromisso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @Column(nullable = false)
    private Instant dataHoraInicio;

    @Column(nullable = false)
    private Instant dataHoraFim;

    private String local;

    // NOVO: Adiciona um campo para rastrear o criador do compromisso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;

    // RELACIONAMENTO ALTERADO: De @ManyToOne para @ManyToMany
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "compromisso_participantes", // Nome da tabela de junção
        joinColumns = @JoinColumn(name = "compromisso_id"), // Chave estrangeira para Compromisso
        inverseJoinColumns = @JoinColumn(name = "usuario_id") // Chave estrangeira para Usuario
    )
    private Set<Usuario> participantes = new HashSet<>();

    // Getters e Setters para os novos e alterados campos
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Usuario getCriador() { return criador; }
    public void setCriador(Usuario criador) { this.criador = criador; }
    public Set<Usuario> getParticipantes() { return participantes; }
    public void setParticipantes(Set<Usuario> participantes) { this.participantes = participantes; }
    
    // ... outros getters e setters (id, descricao, dataHoraInicio, etc.) permanecem os mesmos ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Instant getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Instant dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public Instant getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Instant dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
}
