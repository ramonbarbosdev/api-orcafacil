package com.api_orcafacil.dto.plano;

import java.util.List;

public record PoliticaPlanoResumoDTO(
        Long idPlanoAssinatura,
        String nmPlanoAssinatura,
        String tpStatusAssinatura,
        boolean assinaturaAtiva,
        List<LimitePlanoDTO> limites) {
}
