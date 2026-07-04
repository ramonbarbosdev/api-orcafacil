package com.api_orcafacil.dto.organizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizacaoLogoUrlRequest(
        @NotBlank @Size(max = 512) String logoUrl) {
}
