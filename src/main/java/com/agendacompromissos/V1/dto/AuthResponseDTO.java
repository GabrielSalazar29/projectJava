package com.agendacompromissos.V1.dto;

public class AuthResponseDTO {
    private String token;
    private String type = "Bearer"; // Padrão para indicar o tipo de token

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
