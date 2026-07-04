package com.api_orcafacil.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.orcamento.OrcamentoEnviarRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoEnviarResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.relatorio.orcamento.service.OrcamentoRelatorioService;
import com.api_orcafacil.security.RequerPermissao;
import com.api_orcafacil.service.OrcamentoService;
import com.api_orcafacil.service.VisualizacaoOrcamentoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService.ConteudoLogo;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService service;
    private final VisualizacaoOrcamentoService visualizacaoOrcamentoService;
    private final OrcamentoRelatorioService orcamentoRelatorioService;
    private final OrganizacaoLogoService organizacaoLogoService;

    public OrcamentoController(OrcamentoService service,
            VisualizacaoOrcamentoService visualizacaoOrcamentoService,
            OrcamentoRelatorioService orcamentoRelatorioService,
            OrganizacaoLogoService organizacaoLogoService) {
        this.service = service;
        this.visualizacaoOrcamentoService = visualizacaoOrcamentoService;
        this.orcamentoRelatorioService = orcamentoRelatorioService;
        this.organizacaoLogoService = organizacaoLogoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrcamentoResponse>>> listar() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscar(id)));
    }

    @GetMapping("/sequencia")
    public ResponseEntity<ApiResponseDTO<String>> sequencia() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.sequencia()));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDTO<StatusOrcamento[]>> status() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", StatusOrcamento.values()));
    }

    @PostMapping
    @RequerPermissao(modulo = "orcamentos", acao = "criar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> criar(@Valid @RequestBody OrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Orcamento salvo", service.salvar(request)));
    }

    @PostMapping("/rascunho")
    @RequerPermissao(modulo = "orcamentos", acao = "criar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rascunho(@Valid @RequestBody OrcamentoRequest request) {
        request.setTpStatus(StatusOrcamento.RASCUNHO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Rascunho salvo", service.salvar(request)));
    }

    @PostMapping("/{id}/gerar")
    @RequerPermissao(modulo = "orcamentos", acao = "editar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> gerar(@PathVariable Long id, @Valid @RequestBody OrcamentoRequest request) {
        request.setIdOrcamento(id);
        service.salvar(request);
        OrcamentoResponse response = service.alterarStatus(id, StatusOrcamento.GERADO);
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento gerado", response));
    }

    @PostMapping("/{id}/enviar")
    @RequerPermissao(modulo = "orcamentos", acao = "editar")
    public ResponseEntity<ApiResponseDTO<OrcamentoEnviarResponse>> enviar(
            @PathVariable Long id,
            @RequestBody(required = false) OrcamentoEnviarRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento enviado",
                service.enviarComNotificacao(id, request != null ? request : new OrcamentoEnviarRequest())));
    }

    @PostMapping("/{id}/aprovar")
    @RequerPermissao(modulo = "orcamentos", acao = "editar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento aprovado", service.alterarStatus(id, StatusOrcamento.APROVADO)));
    }

    @PostMapping("/{id}/rejeitar")
    @RequerPermissao(modulo = "orcamentos", acao = "editar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento rejeitado", service.alterarStatus(id, StatusOrcamento.REJEITADO)));
    }

    @PostMapping("/preview-precificacao")
    @RequerPermissao(modulo = "orcamentos", acao = "ler")
    public ResponseEntity<ApiResponseDTO<Map<String, BigDecimal>>> previewPrecificacao(@Valid @RequestBody OrcamentoRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Preview calculado",
                Map.of("valorTotal", service.previewPrecificacao(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> atualizar(@PathVariable Long id, @Valid @RequestBody OrcamentoRequest request) {
        request.setIdOrcamento(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento atualizado", service.salvar(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento excluido", null));
    }

    @GetMapping("/visualizacao/{cdPublico}")
    public ResponseEntity<ApiResponseDTO<OrcamentoVisualizacaoDTO>> visualizacao(@PathVariable String cdPublico) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso",
                visualizacaoOrcamentoService.visualizarPorCdPublico(cdPublico)));
    }

    @GetMapping(value = "/visualizacao/{cdPublico}/logo", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp" })
    public ResponseEntity<byte[]> visualizacaoLogo(@PathVariable String cdPublico) {
        ConteudoLogo conteudo = organizacaoLogoService.obterConteudoPublico(cdPublico);
        return OrganizacaoLogoController.respostaImagem(conteudo);
    }

    @GetMapping(value = "/relatorio/{cdPublico}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> relatorio(@PathVariable String cdPublico) {
        byte[] pdf = orcamentoRelatorioService.gerarPorCdPublico(cdPublico);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=orcamento.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
