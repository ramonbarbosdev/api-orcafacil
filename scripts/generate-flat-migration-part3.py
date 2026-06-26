#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"
FILES = {}

def add(p, c):
    FILES[p] = c

# DTOs
add("dto/cliente/ClienteRequest.java", '''package com.api_orcafacil.dto.cliente;

import com.api_orcafacil.common.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    private Long idCliente;
    private TipoCliente tpCliente;

    @NotBlank
    private String nuCpfcnpj;

    @NotBlank
    private String nmCliente;

    private String dsEmail;
    private String nuTelefone;
    private String nuCep;
    private String dsLogradouro;
    private String dsComplemento;
    private String dsBairro;
    private String dsCidade;
    private String dsEstado;
    private Boolean flAtivo;
    private String dsObservacoes;
}
''')

add("dto/cliente/ClienteResponse.java", '''package com.api_orcafacil.dto.cliente;

import java.time.LocalDateTime;

import com.api_orcafacil.common.TipoCliente;
import com.api_orcafacil.model.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponse {

    private Long idCliente;
    private Long idOrganizacao;
    private TipoCliente tpCliente;
    private String nuCpfcnpj;
    private String nmCliente;
    private String dsEmail;
    private String nuTelefone;
    private String nuCep;
    private String dsLogradouro;
    private String dsComplemento;
    private String dsBairro;
    private String dsCidade;
    private String dsEstado;
    private boolean flAtivo;
    private String dsObservacoes;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static ClienteResponse from(Cliente c) {
        ClienteResponse r = new ClienteResponse();
        r.setIdCliente(c.getIdCliente());
        r.setIdOrganizacao(c.getIdOrganizacao());
        r.setTpCliente(c.getTpCliente());
        r.setNuCpfcnpj(c.getNuCpfcnpj());
        r.setNmCliente(c.getNmCliente());
        r.setDsEmail(c.getDsEmail());
        r.setNuTelefone(c.getNuTelefone());
        r.setNuCep(c.getNuCep());
        r.setDsLogradouro(c.getDsLogradouro());
        r.setDsComplemento(c.getDsComplemento());
        r.setDsBairro(c.getDsBairro());
        r.setDsCidade(c.getDsCidade());
        r.setDsEstado(c.getDsEstado());
        r.setFlAtivo(c.isFlAtivo());
        r.setDsObservacoes(c.getDsObservacoes());
        r.setDtCriacao(c.getDtCriacao());
        r.setDtAtualizacao(c.getDtAtualizacao());
        return r;
    }
}
''')

add("dto/catalogo/CatalogoRequest.java", '''package com.api_orcafacil.dto.catalogo;

import java.math.BigDecimal;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.model.CatalogoCampo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoRequest {

    private Long idCatalogo;
    private TipoItem tpItem;

    @NotBlank
    private String cdCatalogo;

    @NotBlank
    private String nmCatalogo;

    private String dsCatalogo;
    private BigDecimal vlCustoBase;
    private BigDecimal vlPrecoBase;
    private List<CatalogoCampo> campos;
}
''')

add("dto/catalogo/CatalogoResponse.java", '''package com.api_orcafacil.dto.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CatalogoCampo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoResponse {

    private Long idCatalogo;
    private Long idOrganizacao;
    private TipoItem tpItem;
    private String cdCatalogo;
    private String nmCatalogo;
    private String dsCatalogo;
    private BigDecimal vlCustoBase;
    private BigDecimal vlPrecoBase;
    private List<CatalogoCampo> campos;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static CatalogoResponse from(Catalogo c) {
        CatalogoResponse r = new CatalogoResponse();
        r.setIdCatalogo(c.getIdCatalogo());
        r.setIdOrganizacao(c.getIdOrganizacao());
        r.setTpItem(c.getTpItem());
        r.setCdCatalogo(c.getCdCatalogo());
        r.setNmCatalogo(c.getNmCatalogo());
        r.setDsCatalogo(c.getDsCatalogo());
        r.setVlCustoBase(c.getVlCustoBase());
        r.setVlPrecoBase(c.getVlPrecoBase());
        r.setCampos(c.getCampos());
        r.setDtCriacao(c.getDtCriacao());
        r.setDtAtualizacao(c.getDtAtualizacao());
        return r;
    }
}
''')

add("dto/servico/ServicoRequest.java", '''package com.api_orcafacil.dto.servico;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoRequest {

    private Long idServico;
    private Long idCategoriaServico;

    @NotBlank
    private String cdServico;

    @NotBlank
    private String nmServico;

    private String dsServico;
    private BigDecimal vlCusto;
    private BigDecimal vlPreco;
}
''')

add("dto/servico/ServicoResponse.java", '''package com.api_orcafacil.dto.servico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api_orcafacil.model.Servico;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoResponse {

    private Long idServico;
    private Long idOrganizacao;
    private Long idCategoriaServico;
    private String cdServico;
    private String nmServico;
    private String dsServico;
    private BigDecimal vlCusto;
    private BigDecimal vlPreco;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static ServicoResponse from(Servico s) {
        ServicoResponse r = new ServicoResponse();
        r.setIdServico(s.getIdServico());
        r.setIdOrganizacao(s.getIdOrganizacao());
        r.setIdCategoriaServico(s.getIdCategoriaServico());
        r.setCdServico(s.getCdServico());
        r.setNmServico(s.getNmServico());
        r.setDsServico(s.getDsServico());
        r.setVlCusto(s.getVlCusto());
        r.setVlPreco(s.getVlPreco());
        r.setDtCriacao(s.getDtCriacao());
        r.setDtAtualizacao(s.getDtAtualizacao());
        return r;
    }
}
''')

add("dto/precificacao/CampoMetodoDTO.java", '''package com.api_orcafacil.dto.precificacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampoMetodoDTO {

    private String nome;
    private String label;
    private String tipo;
    private boolean obrigatorio;
}
''')

add("dto/precificacao/MetodoPrecificacaoResponse.java", '''package com.api_orcafacil.dto.precificacao;

import java.util.List;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPrecificacaoResponse {

    private Long idMetodoPrecificacao;
    private TipoPrecificacao cdMetodoPrecificacao;
    private String nmMetodoPrecificacao;
    private String dsMetodoPrecificacao;
    private List<CampoMetodoDTO> campos;

    public static MetodoPrecificacaoResponse from(MetodoPrecificacao m, List<CampoMetodoDTO> campos) {
        MetodoPrecificacaoResponse r = new MetodoPrecificacaoResponse();
        r.setIdMetodoPrecificacao(m.getIdMetodoPrecificacao());
        r.setCdMetodoPrecificacao(m.getCdMetodoPrecificacao());
        r.setNmMetodoPrecificacao(m.getNmMetodoPrecificacao());
        r.setDsMetodoPrecificacao(m.getDsMetodoPrecificacao());
        r.setCampos(campos);
        return r;
    }
}
''')

add("dto/precificacao/CampoPersonalizadoRequest.java", '''package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoCampoValor;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampoPersonalizadoRequest {

    private Long idCampoPersonalizado;

    @NotBlank
    private String cdCampoPersonalizado;

    @NotBlank
    private String nmCampoPersonalizado;

    private String dsCampoPersonalizado;

    @NotBlank
    private String tpCampoPersonalizado;

    private TipoCampoValor tpCampoValor;
}
''')

add("dto/precificacao/EmpresaMetodoPrecificacaoRequest.java", '''package com.api_orcafacil.dto.precificacao;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaMetodoPrecificacaoRequest {

    private Long idEmpresaMetodoPrecificacao;
    private Long idMetodoPrecificacao;
    private Map<String, Object> configuracao;
}
''')

add("dto/precificacao/MetodoAjusteRequest.java", '''package com.api_orcafacil.dto.precificacao;

import com.api_orcafacil.common.TipoAjuste;
import com.api_orcafacil.common.TipoOperacaoAjuste;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoAjusteRequest {

    private Long idMetodoAjuste;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCampoPersonalizado;
    private TipoAjuste tpAjuste;
    private TipoOperacaoAjuste tpOperacao;
    private String vlCondicao;
    private Double vlIncremento;
}
''')

add("dto/orcamento/OrcamentoRequest.java", '''package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoRequest {

    private Long idOrcamento;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private Long idCliente;
    private Cliente cliente;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCondicaoPagamento;
    private Integer nuPrazoEntrega;
    private String dsObservacoes;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private StatusOrcamento tpStatus;
    private List<OrcamentoItem> itens;
}
''')

add("dto/orcamento/OrcamentoResponse.java", '''package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoResponse {

    private Long idOrcamento;
    private Long idOrganizacao;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private Long idCliente;
    private String nmCliente;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCondicaoPagamento;
    private String nmCondicaoPagamento;
    private Integer nuPrazoEntrega;
    private String dsObservacoes;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private StatusOrcamento tpStatus;
    private String cdPublico;
    private List<OrcamentoItem> itens;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static OrcamentoResponse from(Orcamento o) {
        OrcamentoResponse r = new OrcamentoResponse();
        r.setIdOrcamento(o.getIdOrcamento());
        r.setIdOrganizacao(o.getIdOrganizacao());
        r.setNuOrcamento(o.getNuOrcamento());
        r.setDtEmissao(o.getDtEmissao());
        r.setDtValido(o.getDtValido());
        r.setIdCliente(o.getIdCliente());
        r.setNmCliente(o.getNmCliente());
        r.setIdEmpresaMetodoPrecificacao(o.getIdEmpresaMetodoPrecificacao());
        r.setIdCondicaoPagamento(o.getIdCondicaoPagamento());
        r.setNmCondicaoPagamento(o.getNmCondicaoPagamento());
        r.setNuPrazoEntrega(o.getNuPrazoEntrega());
        r.setDsObservacoes(o.getDsObservacoes());
        r.setVlPrecoBase(o.getVlPrecoBase());
        r.setVlPrecoFinal(o.getVlPrecoFinal());
        r.setTpStatus(o.getTpStatus());
        r.setCdPublico(o.getCdPublico());
        r.setItens(o.getItens());
        r.setDtCriacao(o.getDtCriacao());
        r.setDtAtualizacao(o.getDtAtualizacao());
        return r;
    }
}
''')

add("dto/orcamento/OrcamentoVisualizacaoDTO.java", '''package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.common.TipoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoVisualizacaoDTO {

    private Long idOrcamento;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private StatusOrcamento status;
    private String nmEmpresa;
    private ClienteVisualizacaoDTO cliente;
    private String metodoPrecificacao;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private List<ItemVisualizacaoDTO> itens;
    private List<StatusHistoricoVisualizacaoDTO> historicoStatus;
    private BigDecimal totalDesconto;
    private String condicaoPagamento;
    private Integer nuPrazoEntrega;
    private String observacoes;

    public List<ItemVisualizacaoDTO> getProdutos() {
        if (itens == null) return List.of();
        return itens.stream().filter(i -> i.getTipo() == TipoItem.Produto).toList();
    }

    public List<ItemVisualizacaoDTO> getServicos() {
        if (itens == null) return List.of();
        return itens.stream().filter(i -> i.getTipo() == TipoItem.Servico).toList();
    }
}
''')

add("dto/orcamento/ItemVisualizacaoDTO.java", '''package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.util.List;

import com.api_orcafacil.common.TipoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemVisualizacaoDTO {

    private Long idItem;
    private String codigo;
    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal precoCusto;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private TipoItem tipo;
    private List<MaterialVisualizacaoDTO> materiais;

    public List<MaterialVisualizacaoDTO> getMateriais() {
        return materiais != null ? materiais : List.of();
    }
}
''')

add("dto/orcamento/MaterialVisualizacaoDTO.java", '''package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;

import com.api_orcafacil.common.TipoCampoValor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialVisualizacaoDTO {

    private String nome;
    private String descricao;
    private BigDecimal valor;
    private TipoCampoValor tipo;
}
''')

add("dto/orcamento/ClienteVisualizacaoDTO.java", '''package com.api_orcafacil.dto.orcamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteVisualizacaoDTO {

    private Long idCliente;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
}
''')

add("dto/orcamento/StatusHistoricoVisualizacaoDTO.java", '''package com.api_orcafacil.dto.orcamento;

import java.time.LocalDateTime;

import com.api_orcafacil.common.StatusOrcamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusHistoricoVisualizacaoDTO {

    private StatusOrcamento statusAnterior;
    private StatusOrcamento statusAtual;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataHora;

    private String usuario;
}
''')

add("dto/perfil/PerfilResponse.java", '''package com.api_orcafacil.dto.perfil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilResponse {

    private Long idUsuario;
    private String login;
    private String nome;
    private String fotoUrl;
    private String role;
}
''')

add("dto/perfil/PerfilRequest.java", '''package com.api_orcafacil.dto.perfil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilRequest {

    private String nome;
    private String senha;
}
''')

add("dto/precificacao/PlanoAssinaturaRequest.java", '''package com.api_orcafacil.dto.precificacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAssinaturaRequest {

    private String nmPlanoAssinatura;
    private Double vlMensal;
    private Integer nuLimiteMensagens;
    private Integer nuLimiteAtendentes;
    private Boolean flAtivo;
}
''')

add("dto/precificacao/PlanoAssinaturaResponse.java", '''package com.api_orcafacil.dto.precificacao;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAssinaturaResponse {

    private Long idPlanoAssinatura;
    private String nmPlanoAssinatura;
    private Double vlMensal;
    private Integer nuLimiteMensagens;
    private Integer nuLimiteAtendentes;
    private Boolean flAtivo;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;
}
''')

add("relatorio/RelatorioRequestBase.java", '''package com.api_orcafacil.relatorio;

public abstract class RelatorioRequestBase {

    private String campoOrdenacao;
    private String direcaoOrdenacao;
    private String formatoSaida = "PDF";

    public String getCampoOrdenacao() { return campoOrdenacao; }
    public void setCampoOrdenacao(String campoOrdenacao) { this.campoOrdenacao = campoOrdenacao; }
    public String getDirecaoOrdenacao() { return direcaoOrdenacao; }
    public void setDirecaoOrdenacao(String direcaoOrdenacao) { this.direcaoOrdenacao = direcaoOrdenacao; }
    public String getFormatoSaida() { return formatoSaida; }
    public void setFormatoSaida(String formatoSaida) { this.formatoSaida = formatoSaida; }
}
''')

if __name__ == "__main__":
    for rel, content in FILES.items():
        (ROOT / rel).parent.mkdir(parents=True, exist_ok=True)
        (ROOT / rel).write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    print(f"Total: {len(FILES)}")
