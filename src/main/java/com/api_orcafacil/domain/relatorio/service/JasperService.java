package com.api_orcafacil.domain.relatorio.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;
import org.springframework.web.bind.annotation.RequestParam;

import com.api_orcafacil.domain.relatorio.dto.RelatorioRequestBase;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class JasperService {

    @Autowired
    private DataSource dataSource;

    private static final String REPORT_FOLDER = "/report/";

    public byte[] exportarGenerico(String templateNome, String formato, Map<String, Object> parametros)
            throws Exception {

        // Caminho JRXML principal
        String caminho = REPORT_FOLDER + templateNome + ".jrxml";
        InputStream jrxmlStream = this.getClass().getResourceAsStream(caminho);
        if (jrxmlStream == null) {
            throw new RuntimeException("Relatório não encontrado: " + templateNome);
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
                default -> throw new IllegalArgumentException("Formato não suportado: " + formato);
            };

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String carregarReportComponent(String componentPath) throws Exception {
        InputStream componentStream = this.getClass().getResourceAsStream(componentPath);
        if (componentStream == null) {
            throw new RuntimeException("Componente do relatório não encontrado: " + componentPath);
        }

        // Cria arquivo temporário
        String extension = componentPath.endsWith(".jrtx") ? ".jrtx" : ".jrxml";
        File tempComponent = File.createTempFile("REPORT_COMPONENT_", extension);

        // Copia conteúdo do JAR para o arquivo temporário
        try (FileOutputStream out = new FileOutputStream(tempComponent)) {
            componentStream.transferTo(out);
        }

        // Retorna o caminho absoluto, que pode ser passado para parâmetros do Jasper
        return tempComponent.getAbsolutePath();
    }

    public String normalizarDataISO(String isoString) {

        if (isoString != null && !isoString.isEmpty()) {
            LocalDate date = LocalDate.parse(isoString.substring(0, 10));
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return null;

    }

    // Versão padrão: valida por padrão
    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(
            T filtro,
            ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo) throws Exception {
        return gerarRelatorio(filtro, gerador, nomeArquivo, true);
    }

    public <T extends RelatorioRequestBase> ResponseEntity<?> gerarRelatorio(
            T filtro,
            ThrowingFunction<T, byte[]> gerador,
            String nomeArquivo,
            Boolean validar) throws Exception {
        try {

            if (Boolean.TRUE.equals(validar)) {
                validarFiltro(filtro);
            }
            
            byte[] relatorio = gerador.apply(filtro);

            String formato = filtro.getFormatoSaida();
            MediaType mediaType = determinarMediaType(formato);
            String extensao = formato.toLowerCase();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + nomeArquivo + "." + extensao + "\"")
                    .contentType(mediaType)
                    .body(relatorio);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public <T extends RelatorioRequestBase> void validarFiltro(T filtro) {
        // validações comuns
        if (filtro.getFormatoSaida() == null || filtro.getFormatoSaida().isBlank()) {
            throw new IllegalArgumentException("O formato de saída é obrigatório (ex: PDF ou XLSX).");
        }

     

        boolean vazio = true;

        try {
            for (Field field : filtro.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(filtro);

                if (value != null) {
                    if (value instanceof String s) {
                        if (!s.isBlank()) {
                            vazio = false;
                            break;
                        }
                    } else if (value instanceof Collection<?> c) {
                        if (!c.isEmpty()) {
                            vazio = false;
                            break;
                        }
                    } else {
                        // Para qualquer outro tipo, só o fato de não ser null já conta
                        vazio = false;
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erro ao validar filtro", e);
        }

        if (vazio) {
            throw new IllegalArgumentException(
                    "É necessário informar pelo menos um filtro.");
        }

    }

    private MediaType determinarMediaType(String formato) {
        return switch (formato.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "XLSX" ->
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}