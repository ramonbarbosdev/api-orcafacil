package com.api_orcafacil.domain.orcamento.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orcamento_item_campo_valor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoItemCampoValor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item_campo_valor")
    @SequenceGenerator(name = "seq_orcamento_item_campo_valor", sequenceName = "seq_orcamento_item_campo_valor", allocationSize = 1)
    @Column(name = "id_orcamentoitemcampovalor")
    private Long idOrcamentoItemCampoValor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamentoitem", nullable = false)
    @JsonIgnore
    private OrcamentoItem orcamentoItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Column(name = "id_campopersonalizado", nullable = false)
    @NotNull(message = "O Campo Personalizado é obrigatorio!")
    private Long idCampoPersonalizado;

    @Column(name = "vl_informado", nullable = false, length = 255)
    @NotNull(message = "O valor é obrigatorio!")
    private String vlInformado;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}
