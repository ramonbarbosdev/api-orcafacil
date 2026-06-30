package com.api_orcafacil.relatorio.engine;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.api_orcafacil.exception.BusinessException;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class JasperBeanRelatorioEngine {

    public byte[] exportarPdf(String classpathJrxml, Map<String, Object> parametros, Collection<?> dados) {
        try (InputStream reportStream = getClass().getResourceAsStream(classpathJrxml)) {
            if (reportStream == null) {
                throw new BusinessException("Modelo do relatorio nao encontrado: " + classpathJrxml);
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parametros,
                    new JRBeanCollectionDataSource(dados));
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Erro ao gerar relatorio: " + ex.getMessage());
        }
    }
}
