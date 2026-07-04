package com.api_orcafacil.controller;

import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.organizacao.OrganizacaoLogoMetadadosDTO;
import com.api_orcafacil.dto.organizacao.OrganizacaoLogoUrlRequest;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;
import com.api_orcafacil.service.logo.OrganizacaoLogoService.ConteudoLogo;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizacao")
public class OrganizacaoLogoController {

    private final OrganizacaoLogoService service;

    public OrganizacaoLogoController(OrganizacaoLogoService service) {
        this.service = service;
    }

    @GetMapping("/logo")
    public ResponseEntity<ApiResponseDTO<OrganizacaoLogoMetadadosDTO>> obterMetadados() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obterMetadadosAtual()));
    }

    @PostMapping("/logo")
    public ResponseEntity<ApiResponseDTO<OrganizacaoLogoMetadadosDTO>> enviarLogo(@RequestParam("file") MultipartFile file)
            throws Exception {
        return ResponseEntity.ok(new ApiResponseDTO<>("Logo atualizada", service.enviarOuSubstituir(file)));
    }

    @GetMapping(value = "/logo/imagem", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp" })
    public ResponseEntity<byte[]> exibirLogo() {
        ConteudoLogo conteudo = service.obterConteudoAtual();
        return respostaImagem(conteudo);
    }

    @DeleteMapping("/logo")
    public ResponseEntity<ApiResponseDTO<OrganizacaoLogoMetadadosDTO>> removerLogo() throws Exception {
        service.remover();
        return ResponseEntity.ok(new ApiResponseDTO<>("Logo removida", service.obterMetadadosAtual()));
    }

    @PutMapping("/logo/url")
    public ResponseEntity<ApiResponseDTO<OrganizacaoLogoMetadadosDTO>> salvarLogoUrl(
            @Valid @RequestBody OrganizacaoLogoUrlRequest request) {
        return ResponseEntity.ok(new ApiResponseDTO<>("URL da logo atualizada", service.salvarLogoUrl(request.logoUrl())));
    }

    @DeleteMapping("/logo/url")
    public ResponseEntity<ApiResponseDTO<OrganizacaoLogoMetadadosDTO>> removerLogoUrl() {
        return ResponseEntity.ok(new ApiResponseDTO<>("URL da logo removida", service.removerLogoUrl()));
    }

    static ResponseEntity<byte[]> respostaImagem(ConteudoLogo conteudo) {
        CacheControl cache = CacheControl.maxAge(Duration.ofHours(1)).cachePublic();
        if (conteudo.atualizadaEm() != null) {
            cache = cache.mustRevalidate();
        }
        return ResponseEntity.ok()
                .cacheControl(cache)
                .header(HttpHeaders.CONTENT_TYPE, conteudo.contentType())
                .body(conteudo.bytes());
    }
}
