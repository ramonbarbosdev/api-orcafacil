package com.api_orcafacil.tenant.central.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissao_global")
public class CentralPermissaoGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_central_permissao")
    @SequenceGenerator(name = "seq_central_permissao", sequenceName = "seq_central_permissao", allocationSize = 1)
    @Column(name = "id_permissao")
    private Long idPermissao;

    @Column(name = "nm_permissao", nullable = false)
    private String nmPermissao;

    @Column(name = "nm_chave", nullable = false)
    private String nmChave;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;
}
