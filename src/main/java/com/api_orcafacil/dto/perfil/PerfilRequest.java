package com.api_orcafacil.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilRequest {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
    private String senha;
}
