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
@IdClass(CentralUsuarioPermissaoId.class)
@Table(name = "usuario_permissao")
public class CentralUsuarioPermissao {

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Id
    @Column(name = "id_organizacao")
    private Long idOrganizacao;

    @Id
    @Column(name = "id_permissao")
    private Long idPermissao;
}
