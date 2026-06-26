package com.api_orcafacil.dto.perfil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilResponse {

    private Long idUsuario;
    private String login;
    private String nome;
    private String fotoUrl;
    private String role;
}
