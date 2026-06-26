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
public class CentralUsuarioPermissaoId implements Serializable {

    private Long idUsuario;
    private Long idOrganizacao;
    private Long idPermissao;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CentralUsuarioPermissaoId that)) {
            return false;
        }
        return Objects.equals(idUsuario, that.idUsuario)
                && Objects.equals(idOrganizacao, that.idOrganizacao)
                && Objects.equals(idPermissao, that.idPermissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idOrganizacao, idPermissao);
    }
}
