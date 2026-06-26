#!/usr/bin/env python3
"""Generate flat structure Java files for api-orcafacil migration."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"

FILES: dict[str, str] = {}

def add(path: str, content: str):
    FILES[path] = content

# --- MODELS ---
add("model/Cliente.java", '''package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "cliente")
public class Cliente extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @SequenceGenerator(name = "seq_cliente", sequenceName = "seq_cliente", allocationSize = 1)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_cliente")
    private TipoCliente tpCliente;

    @Column(name = "nu_cpfcnpj", nullable = false)
    private String nuCpfcnpj;

    @Column(name = "nm_cliente", nullable = false)
    private String nmCliente;

    @Column(name = "ds_email")
    private String dsEmail;

    @Column(name = "nu_telefone")
    private String nuTelefone;

    @Column(name = "nu_cep")
    private String nuCep;

    @Column(name = "ds_logradouro")
    private String dsLogradouro;

    @Column(name = "ds_complemento")
    private String dsComplemento;

    @Column(name = "ds_bairro")
    private String dsBairro;

    @Column(name = "ds_cidade")
    private String dsCidade;

    @Column(name = "ds_estado", length = 2)
    private String dsEstado;

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;

    @Column(name = "ds_observacoes", columnDefinition = "text")
    private String dsObservacoes;

    @Column(name = "id_usuario")
    private Long idUsuario;
}
''')

add("model/Catalogo.java", '''package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalogo")
public class Catalogo extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo")
    @SequenceGenerator(name = "seq_catalogo", sequenceName = "seq_catalogo", allocationSize = 1)
    @Column(name = "id_catalogo")
    private Long idCatalogo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_item")
    private TipoItem tpItem;

    @Column(name = "cd_catalogo", nullable = false, length = 50)
    private String cdCatalogo;

    @Column(name = "nm_catalogo", nullable = false)
    private String nmCatalogo;

    @Column(name = "ds_catalogo", columnDefinition = "text")
    private String dsCatalogo;

    @Column(name = "vl_custobase", precision = 18, scale = 4)
    private BigDecimal vlCustoBase;

    @Column(name = "vl_precobase", precision = 18, scale = 4)
    private BigDecimal vlPrecoBase;

    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CatalogoCampo> campos = new ArrayList<>();
}
''')

add("model/CatalogoCampo.java", '''package com.api_orcafacil.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalogo_campo")
public class CatalogoCampo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo_campo")
    @SequenceGenerator(name = "seq_catalogo_campo", sequenceName = "seq_catalogo_campo", allocationSize = 1)
    @Column(name = "id_catalogo_campo")
    private Long idCatalogoCampo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", nullable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @Column(name = "vl_padrao")
    private String vlPadrao;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    @Column(name = "fl_editavel", nullable = false)
    private Boolean flEditavel = true;

    @Column(name = "ordem")
    private Integer ordem;
}
''')

add("model/CategoriaServico.java", '''package com.api_orcafacil.model;

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
@Table(name = "categoria_servico")
public class CategoriaServico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categoriaservico")
    @SequenceGenerator(name = "seq_categoriaservico", sequenceName = "seq_categoriaservico", allocationSize = 1)
    @Column(name = "id_categoriaservico")
    private Long idCategoriaServico;

    @Column(name = "cd_categoriaservico", nullable = false, length = 50)
    private String cdCategoriaServico;

    @Column(name = "nm_categoriaservico", nullable = false)
    private String nmCategoriaServico;

    @Column(name = "ds_categoriaservico", columnDefinition = "text")
    private String dsCategoriaServico;
}
''')

add("model/Servico.java", '''package com.api_orcafacil.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "servico")
public class Servico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_servico")
    @SequenceGenerator(name = "seq_servico", sequenceName = "seq_servico", allocationSize = 1)
    @Column(name = "id_servico")
    private Long idServico;

    @Column(name = "id_categoriaservico")
    private Long idCategoriaServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoriaservico", insertable = false, updatable = false)
    @JsonIgnore
    private CategoriaServico categoriaServico;

    @Column(name = "cd_servico", nullable = false, length = 50)
    private String cdServico;

    @Column(name = "nm_servico", nullable = false)
    private String nmServico;

    @Column(name = "ds_servico", columnDefinition = "text")
    private String dsServico;

    @Column(name = "vl_custo", precision = 18, scale = 4)
    private BigDecimal vlCusto;

    @Column(name = "vl_preco", precision = 18, scale = 4)
    private BigDecimal vlPreco;
}
''')

add("model/MetodoPrecificacao.java", '''package com.api_orcafacil.model;

import java.util.List;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.CampoMetodoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "metodo_precificacao")
public class MetodoPrecificacao extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodoprecificacao")
    @SequenceGenerator(name = "seq_metodoprecificacao", sequenceName = "seq_metodoprecificacao", allocationSize = 1)
    @Column(name = "id_metodoprecificacao")
    private Long idMetodoPrecificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "cd_metodoprecificacao", nullable = false, length = 30)
    private TipoPrecificacao cdMetodoPrecificacao;

    @Column(name = "nm_metodoprecificacao", nullable = false)
    private String nmMetodoPrecificacao;

    @Column(name = "ds_metodoprecificacao", columnDefinition = "text")
    private String dsMetodoPrecificacao;

    @Transient
    @JsonProperty("campos")
    private List<CampoMetodoDTO> campos;
}
''')

add("model/CampoPersonalizado.java", '''package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoCampoValor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "campos_personalizados")
public class CampoPersonalizado extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_campo_personalizado")
    @SequenceGenerator(name = "seq_campo_personalizado", sequenceName = "seq_campo_personalizado", allocationSize = 1)
    @Column(name = "id_campopersonalizado")
    private Long idCampoPersonalizado;

    @Column(name = "cd_campopersonalizado", nullable = false, length = 50)
    private String cdCampoPersonalizado;

    @Column(name = "nm_campopersonalizado", nullable = false, length = 100)
    private String nmCampoPersonalizado;

    @Column(name = "ds_campopersonalizado")
    private String dsCampoPersonalizado;

    @Column(name = "tp_campopersonalizado", nullable = false, length = 20)
    private String tpCampoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_campovalor", nullable = false, length = 30)
    private TipoCampoValor tpCampoValor = TipoCampoValor.PRECO_FIXO;
}
''')

add("model/EmpresaMetodoPrecificacao.java", '''package com.api_orcafacil.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.api_orcafacil.common.TipoPrecificacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresa_metodo_precificacao")
public class EmpresaMetodoPrecificacao extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_empresametodoprecificacao")
    @SequenceGenerator(name = "seq_empresametodoprecificacao", sequenceName = "seq_empresametodoprecificacao", allocationSize = 1)
    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @Column(name = "id_metodoprecificacao", nullable = false)
    private Long idMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private MetodoPrecificacao metodoPrecificacao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracao", columnDefinition = "jsonb")
    private Map<String, Object> configuracao;

    @JsonProperty("nmMetodoPrecificacao")
    public String getNmMetodoPrecificacao() {
        return metodoPrecificacao != null ? metodoPrecificacao.getNmMetodoPrecificacao() : null;
    }

    @JsonProperty("cdMetodoPrecificacao")
    public TipoPrecificacao getCdMetodoPrecificacao() {
        return metodoPrecificacao != null ? metodoPrecificacao.getCdMetodoPrecificacao() : null;
    }
}
''')

add("model/MetodoAjuste.java", '''package com.api_orcafacil.model;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "metodo_ajustes")
public class MetodoAjuste extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodo_ajuste")
    @SequenceGenerator(name = "seq_metodo_ajuste", sequenceName = "seq_metodo_ajuste", allocationSize = 1)
    @Column(name = "id_metodoajuste")
    private Long idMetodoAjuste;

    @Column(name = "id_empresametodoprecificacao", nullable = false)
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_ajuste", nullable = false, length = 30)
    private TipoAjuste tpAjuste;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_operacao", nullable = false, length = 30)
    private TipoOperacaoAjuste tpOperacao;

    @Column(name = "vl_condicao")
    private String vlCondicao;

    @Column(name = "vl_incremento", nullable = false)
    private Double vlIncremento;

    @JsonProperty("nmCampoPersonalizado")
    public String getNmCampoPersonalizado() {
        return campoPersonalizado != null ? campoPersonalizado.getNmCampoPersonalizado() : null;
    }
}
''')

add("model/CondicaoPagamento.java", '''package com.api_orcafacil.model;

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
''')

add("model/ConfiguracaoOrcamento.java", '''package com.api_orcafacil.model;

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
@Table(name = "configuracao_orcamento")
public class ConfiguracaoOrcamento extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_configuracaoorcamento")
    @SequenceGenerator(name = "seq_configuracaoorcamento", sequenceName = "seq_configuracaoorcamento", allocationSize = 1)
    @Column(name = "id_configuracaoorcamento")
    private Long idConfiguracaoOrcamento;

    @Column(name = "prefixo_numero", nullable = false, length = 10)
    private String prefixoNumero;

    @Column(name = "validade_dias", nullable = false)
    private Integer validadeDias;

    @Column(name = "termos_padrao", columnDefinition = "text")
    private String termosPadrao;
}
''')

add("model/Orcamento.java", '''package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento")
public class Orcamento extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento")
    @SequenceGenerator(name = "seq_orcamento", sequenceName = "seq_orcamento", allocationSize = 1)
    @Column(name = "id_orcamento")
    private Long idOrcamento;

    @Column(name = "nu_orcamento", nullable = false, length = 50)
    private String nuOrcamento;

    @Column(name = "dt_emissao", nullable = false)
    private LocalDate dtEmissao;

    @Column(name = "dt_valido", nullable = false)
    private LocalDate dtValido;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    @Column(name = "id_empresametodoprecificacao")
    private Long idEmpresaMetodoPrecificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresametodoprecificacao", insertable = false, updatable = false)
    @JsonIgnore
    private EmpresaMetodoPrecificacao empresaMetodoPrecificacao;

    @Column(name = "id_codicaopagamento", nullable = false)
    private Long idCondicaoPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_codicaopagamento", insertable = false, updatable = false)
    @JsonIgnore
    private CondicaoPagamento condicaoPagamento;

    @Column(name = "nu_prazoentrega", nullable = false)
    private Integer nuPrazoEntrega = 20;

    @Column(name = "ds_observacoes", columnDefinition = "text")
    private String dsObservacoes;

    @Column(name = "vl_precobase", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoBase;

    @Column(name = "vl_precofinal", precision = 18, scale = 4)
    private BigDecimal vlPrecoFinal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false, length = 30)
    private StatusOrcamento tpStatus = StatusOrcamento.RASCUNHO;

    @Column(name = "cd_publico", unique = true, length = 64)
    private String cdPublico;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrcamentoItem> itens = new ArrayList<>();

    @JsonProperty("nmCliente")
    public String getNmCliente() {
        return cliente != null ? cliente.getNmCliente() : null;
    }

    @JsonProperty("nmCondicaoPagamento")
    public String getNmCondicaoPagamento() {
        return condicaoPagamento != null ? condicaoPagamento.getNmCondicaoPagamento() : null;
    }
}
''')

add("model/OrcamentoItem.java", '''package com.api_orcafacil.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_item")
public class OrcamentoItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item")
    @SequenceGenerator(name = "seq_orcamento_item", sequenceName = "seq_orcamento_item", allocationSize = 1)
    @Column(name = "id_orcamentoitem")
    private Long idOrcamentoItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Column(name = "id_catalogo", nullable = false)
    private Long idCatalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", insertable = false, updatable = false)
    @JsonIgnore
    private Catalogo catalogo;

    @Column(name = "qt_item", nullable = false, precision = 18, scale = 4)
    private BigDecimal qtItem;

    @Column(name = "vl_custounitario", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlCustoUnitario;

    @Column(name = "vl_precounitario", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoUnitario;

    @Column(name = "vl_precototal", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlPrecoTotal;

    @OneToMany(mappedBy = "orcamentoItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrcamentoItemCampoValor> camposValor = new ArrayList<>();

    public String getCdCatalogo() {
        return catalogo != null ? catalogo.getCdCatalogo() : null;
    }

    public String getNmCatalogo() {
        return catalogo != null ? catalogo.getNmCatalogo() : null;
    }

    @Transient
    public TipoItem getTpItem() {
        return catalogo != null ? catalogo.getTpItem() : null;
    }
}
''')

add("model/OrcamentoItemCampoValor.java", '''package com.api_orcafacil.model;

import java.math.BigDecimal;

import com.api_orcafacil.common.TipoCampoValor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_item_campo_valor")
public class OrcamentoItemCampoValor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_item_campo_valor")
    @SequenceGenerator(name = "seq_orcamento_item_campo_valor", sequenceName = "seq_orcamento_item_campo_valor", allocationSize = 1)
    @Column(name = "id_orcamentoitemcampovalor")
    private Long idOrcamentoItemCampoValor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamentoitem", nullable = false)
    @JsonIgnore
    private OrcamentoItem orcamentoItem;

    @Column(name = "id_campopersonalizado", nullable = false)
    private Long idCampoPersonalizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campopersonalizado", insertable = false, updatable = false)
    @JsonIgnore
    private CampoPersonalizado campoPersonalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_valor", nullable = false, length = 30)
    private TipoCampoValor tpValor = TipoCampoValor.PRECO_FIXO;

    @Column(name = "vl_informado", nullable = false, precision = 18, scale = 4)
    private BigDecimal vlInformado;

    @Column(name = "ds_descricao")
    private String dsDescricao;

    public String getNmCampoPersonalizado() {
        return campoPersonalizado != null ? campoPersonalizado.getNmCampoPersonalizado() : null;
    }
}
''')

add("model/OrcamentoStatusHistorico.java", '''package com.api_orcafacil.model;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orcamento_status_historico")
public class OrcamentoStatusHistorico extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orcamento_status_historico")
    @SequenceGenerator(name = "seq_orcamento_status_historico", sequenceName = "seq_orcamento_status_historico", allocationSize = 1)
    @Column(name = "id_orcamentostatushistorico")
    private Long idOrcamentoStatusHistorico;

    @Column(name = "id_orcamento", nullable = false)
    private Long idOrcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orcamento", insertable = false, updatable = false)
    @JsonIgnore
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status_anterior", length = 30)
    private StatusOrcamento tpStatusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status_novo", nullable = false, length = 30)
    private StatusOrcamento tpStatusNovo;
}
''')

# Continue in part 2 - write file and run
if __name__ == "__main__":
    for rel, content in FILES.items():
        path = ROOT / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    print(f"Total: {len(FILES)}")
