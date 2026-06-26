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
@IdClass(CentralPlanoPermissaoId.class)
@Table(name = "plano_permissao")
public class CentralPlanoPermissao {

    @Id
    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura;

    @Id
    @Column(name = "id_permissao")
    private Long idPermissao;

    public CentralPlanoPermissao(Long idPlanoAssinatura, Long idPermissao) {
        this.idPlanoAssinatura = idPlanoAssinatura;
        this.idPermissao = idPermissao;
    }
}
