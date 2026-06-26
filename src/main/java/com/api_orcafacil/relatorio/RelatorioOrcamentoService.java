package com.api_orcafacil.relatorio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.VisualizacaoOrcamentoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RelatorioOrcamentoService {

    private final VisualizacaoOrcamentoService visualizacaoOrcamento;
    private final OrcamentoRepository repository;
    private final OrganizacaoLogoService organizacaoLogoService;

    public RelatorioOrcamentoService(VisualizacaoOrcamentoService visualizacaoOrcamento,
            OrcamentoRepository repository,
            OrganizacaoLogoService organizacaoLogoService) {
        this.visualizacaoOrcamento = visualizacaoOrcamento;
        this.repository = repository;
        this.organizacaoLogoService = organizacaoLogoService;
    }

    public byte[] gerarRelatorioOrcamento(String cdPublico) throws Exception {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orcamento nao encontrado"));
        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        OrcamentoVisualizacaoDTO dto = visualizacaoOrcamento.visualizarPublico(
                orcamento.getIdOrcamento(), orcamento.getIdOrganizacao());
        InputStream reportStream = getClass().getResourceAsStream("/orcaReport/orca.jrxml");
        if (reportStream == null) {
            throw new RuntimeException("Arquivo JRXML nao encontrado em /orcaReport/orca.jrxml");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        Map<String, Object> params = new HashMap<>();
        params.put("TITULO_HEADER", "");
        String logoPath = organizacaoLogoService.resolverCaminhoFisicoParaRelatorio(orcamento.getIdOrganizacao());
        if (logoPath != null && !logoPath.isBlank()) {
            params.put("LOGO_PATH", logoPath);
        }
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
                new JRBeanCollectionDataSource(List.of(dto)));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
