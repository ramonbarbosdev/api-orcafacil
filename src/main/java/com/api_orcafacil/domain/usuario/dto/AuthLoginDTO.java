package com.api_orcafacil.domain.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Autenticar um  usuário")
public class AuthLoginDTO {

    @Schema(description = "Login do usuário", example = "85778905548", required = true)
    private String login;

    @Schema(description = "Senha do usuário", example = "ramon", required = true)
    private String senha;

    @Schema(description = "Inquilino", example = "Clinica", required = true)
    private String idTenant;

    private Boolean isAreaDev;

    // Getters e Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIdTenant() {
        return idTenant;
    }

    public void setIdTenant(String idTenant) {
        this.idTenant = idTenant;
    }

    public Boolean getIsAreaDev() {
        return isAreaDev;
    }

    public void setIsAreaDev(Boolean isAreaDev) {
        this.isAreaDev = isAreaDev;
    }

}