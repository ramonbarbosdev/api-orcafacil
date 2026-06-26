package com.api_orcafacil.model;

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
@Table(name = "codicao_pagamento")
public class CondicaoPagamento extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_codicao_pagamento")
    @SequenceGenerator(name = "seq_codicao_pagamento", sequenceName = "seq_codicao_pagamento", allocationSize = 1)
    @Column(name = "id_codicaopagamento")
    private Long idCondicaoPagamento;

    @Column(name = "cd_codicaopagamento", nullable = false, length = 50)
    private String cdCondicaoPagamento;

    @Column(name = "nm_codicaopagamento", nullable = false)
    private String nmCondicaoPagamento;
}
