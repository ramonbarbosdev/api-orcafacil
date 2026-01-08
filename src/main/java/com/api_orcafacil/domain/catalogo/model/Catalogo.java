package com.api_orcafacil.domain.catalogo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalogo")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo")
    @SequenceGenerator(name = "seq_catalogo", sequenceName = "seq_catalogo", allocationSize = 1)
    @Column(name = "id_catalogo")
    private Long idCatalogo;

    @Column(name = "id_tenant", nullable = false)
    @NotBlank(message = "O tenant é obrigatorio!")
    private String idTenant;

    @NotBlank(message = "O código é obrigatorio!")
    @Column(name = "cd_catalogo")
    private String cdCatalogo;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_catalogo")
    private String nmCatalogo;

    @Column(name = "ds_catalogo")
    private String dsCatalogo;

    @Column(name = "vl_custobase", precision = 18, scale = 4)
    private BigDecimal vlCustoBase;

    @Column(name = "vl_precobase", precision = 18, scale = 4)
    private BigDecimal vlPrecoBase;

    @OneToMany(mappedBy = "idCatalogo", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = false)
    public List<CatalogoCampo> catalogoCampo = new ArrayList<CatalogoCampo>();

    @Column(name = "dt_cadastro", updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
