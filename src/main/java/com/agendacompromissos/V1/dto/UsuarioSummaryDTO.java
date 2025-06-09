package com.agendacompromissos.V1.dto;

public class UsuarioSummaryDTO {
    private Long id;
    private String username;
    // Adicione outros campos que queira exibir, como foto de perfil, se tiver

    public UsuarioSummaryDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
