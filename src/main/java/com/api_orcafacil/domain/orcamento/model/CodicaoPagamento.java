package com.api_orcafacil.domain.orcamento.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "codicao_pagamento")
public class CodicaoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_codicao_pagamento")
    @SequenceGenerator(name = "seq_codicao_pagamento", sequenceName = "seq_codicao_pagamento", allocationSize = 1)
    @Column(name = "id_codicaopagamento")
    private Long idCondicaoPagamento;

    @NotBlank(message = "O código é obrigatorio!")
    @Column(name = "cd_codicaopagamento")
    private String cdCondicaoPagamento;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_codicaopagamento")
    private String nmCondicaoPagamento;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
