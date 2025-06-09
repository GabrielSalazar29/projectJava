package com.agendacompromissos.V1.dto;

public class AuthResponseDTO {

    private String token;
    private UsuarioSummaryDTO user; // Usa o DTO que já criámos para o resumo do utilizador

    public AuthResponseDTO(String token, UsuarioSummaryDTO user) {
        this.token = token;
        this.user = user;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioSummaryDTO getUser() {
        return user;
    }

    public void setUser(UsuarioSummaryDTO user) {
        this.user = user;
    }
}