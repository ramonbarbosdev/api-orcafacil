package com.api_orcafacil.tenant.central.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CentralPlanoPermissaoId implements Serializable {

    private Long idPlanoAssinatura;
    private Long idPermissao;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CentralPlanoPermissaoId that)) {
            return false;
        }
        return Objects.equals(idPlanoAssinatura, that.idPlanoAssinatura)
                && Objects.equals(idPermissao, that.idPermissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlanoAssinatura, idPermissao);
    }
}
