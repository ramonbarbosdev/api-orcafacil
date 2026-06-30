package com.api_orcafacil.relatorio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.OrcamentoPublicoService;
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
    private final ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService;

    public RelatorioOrcamentoService(VisualizacaoOrcamentoService visualizacaoOrcamento,
            OrcamentoRepository repository,
            OrganizacaoLogoService organizacaoLogoService,
            ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService) {
        this.visualizacaoOrcamento = visualizacaoOrcamento;
        this.repository = repository;
        this.organizacaoLogoService = organizacaoLogoService;
        this.orcamentoPublicoService = orcamentoPublicoService;
    }

    public byte[] gerarRelatorioOrcamento(String cdPublico) {
        OrcamentoPublicoService publicoService = orcamentoPublicoService.getIfAvailable();
        if (publicoService == null) {
            return gerarRelatorioInterno(cdPublico);
        }
        return publicoService.executarComCdPublico(cdPublico, ref -> gerarRelatorioInterno(ref.cdPublico()));
    }

    private byte[] gerarRelatorioInterno(String cdPublico) {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResourceNotFoundException("Orcamento nao encontrado");
        }
        OrcamentoVisualizacaoDTO dto = visualizacaoOrcamento.visualizarPublico(
                orcamento.getIdOrcamento(), orcamento.getIdOrganizacao());
        try {
            InputStream reportStream = getClass().getResourceAsStream("/orcaReport/orca.jrxml");
            if (reportStream == null) {
                throw new BusinessException("Modelo do relatorio nao encontrado");
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
        } catch (BusinessException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Erro ao gerar relatorio do orcamento: " + ex.getMessage());
        }
    }
}
