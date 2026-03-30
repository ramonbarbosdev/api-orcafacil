package com.api_orcafacil.domain.relatorio.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.domain.orcamento.dto.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoRepository;
import com.api_orcafacil.domain.orcamento.service.VisualizacaoOrcamentoService;
import com.api_orcafacil.enums.StatusOrcamento;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RelatorioOrcamentoService {

    @Autowired
    private JasperService jasperService;

    @Autowired
    private VisualizacaoOrcamentoService visualizacaoOrcamento;

    @Autowired
    private OrcamentoRepository repository;

    public byte[] gerarRelatorioOrcamento(String cdPublico) throws Exception {

        Orcamento orcamento = repository
                .findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Orçamento não encontrado"));

        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        OrcamentoVisualizacaoDTO dto = visualizacaoOrcamento.visualizarPublico(orcamento.getIdOrcamento(),
                orcamento.getIdTenant());

        InputStream reportStream = getClass()
                .getResourceAsStream("/orcaReport/orcamento_layout_danfe.jrxml");

        if (reportStream == null) {
            throw new RuntimeException("Arquivo JRXML não encontrado em /orcaReport/orca.jrxml");
        }

        

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> params = new HashMap<>();

  
        params.put("TITULO_HEADER", "");


        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(dto));

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                params,
                dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
