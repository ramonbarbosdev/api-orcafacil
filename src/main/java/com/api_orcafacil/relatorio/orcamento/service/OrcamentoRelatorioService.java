package com.api_orcafacil.relatorio.orcamento.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.relatorio.engine.JasperBeanRelatorioEngine;
import com.api_orcafacil.relatorio.orcamento.dto.OrcamentoRelatorioDTO;
import com.api_orcafacil.relatorio.orcamento.mapper.OrcamentoRelatorioMapper;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.OrcamentoPublicoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;

@Service
public class OrcamentoRelatorioService {

    private static final String TEMPLATE_CLASSPATH = "/relatorio/orcamento/orcamento.jrxml";

    private final OrcamentoRepository repository;
    private final OrcamentoRelatorioMapper mapper;
    private final JasperBeanRelatorioEngine jasperEngine;
    private final OrganizacaoLogoService organizacaoLogoService;
    private final ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService;
    private final ObjectProvider<NamedParameterJdbcTemplate> centralJdbc;
    private final ObjectProvider<OrcamentoRelatorioService> self;

    public OrcamentoRelatorioService(OrcamentoRepository repository,
            OrcamentoRelatorioMapper mapper,
            JasperBeanRelatorioEngine jasperEngine,
            OrganizacaoLogoService organizacaoLogoService,
            ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService,
            ObjectProvider<NamedParameterJdbcTemplate> centralJdbc,
            ObjectProvider<OrcamentoRelatorioService> self) {
        this.repository = repository;
        this.mapper = mapper;
        this.jasperEngine = jasperEngine;
        this.organizacaoLogoService = organizacaoLogoService;
        this.orcamentoPublicoService = orcamentoPublicoService;
        this.centralJdbc = centralJdbc;
        this.self = self;
    }

    public byte[] gerarPorCdPublico(String cdPublico) {
        OrcamentoPublicoService publicoService = orcamentoPublicoService.getIfAvailable();
        if (publicoService == null) {
            return self.getObject().gerarComTransacao(cdPublico);
        }
        return publicoService.executarComCdPublico(cdPublico,
                ref -> self.getObject().gerarComTransacao(ref.cdPublico()));
    }

    @Transactional(readOnly = true)
    public byte[] gerarComTransacao(String cdPublico) {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));

        inicializarAssociacoes(orcamento);
        String nomeOrganizacao = buscarNomeOrganizacao(orcamento.getIdOrganizacao());
        OrcamentoRelatorioDTO dto = mapper.mapear(orcamento, nomeOrganizacao);

        Map<String, Object> parametros = new HashMap<>();
        String logoPath = organizacaoLogoService.resolverCaminhoFisicoParaRelatorio(orcamento.getIdOrganizacao());
        if (logoPath != null && !logoPath.isBlank()) {
            parametros.put("LOGO_PATH", logoPath);
        }

        return jasperEngine.exportarPdf(TEMPLATE_CLASSPATH, parametros, List.of(dto));
    }

    private void inicializarAssociacoes(Orcamento orcamento) {
        orcamento.getNmCondicaoPagamento();
        orcamento.getNmCliente();
        Cliente cliente = orcamento.getCliente();
        if (cliente != null) {
            cliente.getNmCliente();
            cliente.getNuCpfcnpj();
        }
        List<OrcamentoItem> itens = orcamento.getItens();
        if (itens == null) {
            return;
        }
        for (OrcamentoItem item : itens) {
            item.getCdCatalogo();
            item.getNmCatalogo();
            item.getTpItem();
            List<OrcamentoItemCampoValor> campos = item.getCamposValor();
            if (campos != null) {
                for (OrcamentoItemCampoValor campo : campos) {
                    campo.getNmCampoPersonalizado();
                }
            }
        }
    }

    private String buscarNomeOrganizacao(Long idOrganizacao) {
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc == null) {
            return null;
        }
        try {
            return jdbc.queryForObject(
                    "select nm_organizacao from organizacao where id_organizacao = :id",
                    Map.of("id", idOrganizacao),
                    String.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
