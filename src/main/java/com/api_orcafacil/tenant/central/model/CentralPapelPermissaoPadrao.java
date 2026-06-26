package com.api_orcafacil.tenant.central.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(CentralPapelPermissaoPadraoId.class)
@Table(name = "papel_permissao_padrao")
public class CentralPapelPermissaoPadrao {

    @Id
    @Column(name = "id_papel")
    private Long idPapel;

    @Id
    @Column(name = "id_permissao")
    private Long idPermissao;

    public CentralPapelPermissaoPadrao(Long idPapel, Long idPermissao) {
        this.idPapel = idPapel;
        this.idPermissao = idPermissao;
    }
}
