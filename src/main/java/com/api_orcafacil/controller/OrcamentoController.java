package com.api_orcafacil.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoResponse;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.relatorio.RelatorioOrcamentoService;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.OrcamentoService;
import com.api_orcafacil.service.VisualizacaoOrcamentoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService.ConteudoLogo;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService service;
    private final OrcamentoRepository repository;
    private final VisualizacaoOrcamentoService visualizacaoOrcamentoService;
    private final RelatorioOrcamentoService relatorioOrcamentoService;
    private final OrganizacaoLogoService organizacaoLogoService;

    public OrcamentoController(OrcamentoService service, OrcamentoRepository repository,
            VisualizacaoOrcamentoService visualizacaoOrcamentoService,
            RelatorioOrcamentoService relatorioOrcamentoService,
            OrganizacaoLogoService organizacaoLogoService) {
        this.service = service;
        this.repository = repository;
        this.visualizacaoOrcamentoService = visualizacaoOrcamentoService;
        this.relatorioOrcamentoService = relatorioOrcamentoService;
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
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> criar(@RequestBody OrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Orcamento salvo", service.salvar(request)));
    }

    @PostMapping("/rascunho")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rascunho(@RequestBody OrcamentoRequest request) {
        request.setTpStatus(StatusOrcamento.RASCUNHO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Rascunho salvo", service.salvar(request)));
    }

    @PostMapping("/{id}/gerar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> gerar(@PathVariable Long id, @RequestBody OrcamentoRequest request) {
        request.setIdOrcamento(id);
        request.setTpStatus(StatusOrcamento.GERADO);
        OrcamentoResponse response = service.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Orcamento gerado", response));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento enviado", service.alterarStatus(id, StatusOrcamento.ENVIADO)));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento aprovado", service.alterarStatus(id, StatusOrcamento.APROVADO)));
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Orcamento rejeitado", service.alterarStatus(id, StatusOrcamento.REJEITADO)));
    }

    @PostMapping("/preview-precificacao")
    public ResponseEntity<ApiResponseDTO<Map<String, BigDecimal>>> previewPrecificacao(@RequestBody OrcamentoRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Preview calculado",
                Map.of("valorTotal", service.previewPrecificacao(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<OrcamentoResponse>> atualizar(@PathVariable Long id, @RequestBody OrcamentoRequest request) {
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
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orcamento nao encontrado"));
        if (orcamento.getTpStatus() == StatusOrcamento.RASCUNHO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso",
                visualizacaoOrcamentoService.visualizarPorCdPublico(cdPublico)));
    }

    @GetMapping(value = "/visualizacao/{cdPublico}/logo", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp" })
    public ResponseEntity<byte[]> visualizacaoLogo(@PathVariable String cdPublico) {
        ConteudoLogo conteudo = organizacaoLogoService.obterConteudoPublico(cdPublico);
        return OrganizacaoLogoController.respostaImagem(conteudo);
    }

    @GetMapping(value = "/relatorio/{cdPublico}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> relatorio(@PathVariable String cdPublico) throws Exception {
        byte[] pdf = relatorioOrcamentoService.gerarRelatorioOrcamento(cdPublico);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=orcamento.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
