package com.api_orcafacil.dto.organizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizacaoEmpresaRequestDTO(
        @NotBlank(message = "O documento da empresa e obrigatorio")
        @Size(max = 20, message = "Documento invalido")
        String cdEmpresa,
        @NotBlank(message = "O nome da empresa e obrigatorio")
        @Size(max = 255, message = "Nome da empresa muito longo")
        String nmEmpresa,
        @Size(max = 255, message = "E-mail muito longo")
        String dsEmail,
        @Size(max = 30, message = "Telefone muito longo")
        String nuTelefone) {
}
