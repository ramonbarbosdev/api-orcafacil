package com.api_orcafacil.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class JasperService {

    private static final String REPORT_FOLDER = "/report/";
    private final DataSource dataSource;

    public JasperService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] exportarGenerico(String templateNome, String formato, Map<String, Object> parametros) throws Exception {
        String caminho = REPORT_FOLDER + templateNome + ".jrxml";
        InputStream jrxmlStream = getClass().getResourceAsStream(caminho);
        if (jrxmlStream == null) {
            throw new RuntimeException("Relatorio nao encontrado: " + templateNome);
        }
        parametros.put("REPORT_STYLE", carregarReportComponent(REPORT_FOLDER + "style/RelatorioStyles.jrtx"));
        parametros.put("ICON_PATH", carregarReportComponent(REPORT_FOLDER + "icone.png"));
        try (Connection connection = dataSource.getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            return switch (formato.toUpperCase()) {
                case "PDF" -> JasperExportManager.exportReportToPdf(jasperPrint);
                case "XLSX" -> {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                    exporter.exportReport();
                    yield outputStream.toByteArray();
                }
                default -> throw new IllegalArgumentException("Formato nao suportado: " + formato);
            };
        }
    }

    public String carregarReportComponent(String componentPath) throws Exception {
        InputStream componentStream = getClass().getResourceAsStream(componentPath);
        if (componentStream == null) {
            throw new RuntimeException("Componente do relatorio nao encontrado: " + componentPath);
        }
        String extension = componentPath.endsWith(".jrtx") ? ".jrtx" : ".jrxml";
        File tempComponent = File.createTempFile("REPORT_COMPONENT_", extension);
        try (FileOutputStream out = new FileOutputStream(tempComponent)) {
            componentStream.transferTo(out);
        }
        return tempComponent.getAbsolutePath();
    }

    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(T filtro, ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo) throws Exception {
        return gerarRelatorio(filtro, gerador, nomeArquivo, true);
    }

    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(T filtro, ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo, Boolean validar) throws Exception {
        if (Boolean.TRUE.equals(validar)) {
            validarFiltro(filtro);
        }
        byte[] relatorio = gerador.apply(filtro);
        String formato = filtro.getFormatoSaida();
        MediaType mediaType = determinarMediaType(formato);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nomeArquivo + "." + formato.toLowerCase() + "\"")
                .contentType(mediaType)
                .body(relatorio);
    }

    public <T extends RelatorioRequestBase> void validarFiltro(T filtro) {
        if (filtro.getFormatoSaida() == null || filtro.getFormatoSaida().isBlank()) {
            throw new IllegalArgumentException("O formato de saida e obrigatorio");
        }
    }

    private MediaType determinarMediaType(String formato) {
        return switch (formato.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "XLSX" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
