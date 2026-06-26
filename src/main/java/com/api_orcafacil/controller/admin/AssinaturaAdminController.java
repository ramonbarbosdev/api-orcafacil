package com.api_orcafacil.controller.admin;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.plano.AssinaturaResponseDTO;
import com.api_orcafacil.dto.plano.AssinaturaStatusUpdateDTO;
import com.api_orcafacil.dto.plano.OrganizacaoPlanoUpdateDTO;
import com.api_orcafacil.dto.plano.PoliticaPlanoResumoDTO;
import com.api_orcafacil.service.AssinaturaPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class AssinaturaAdminController {

    private final AssinaturaPlatformService service;

    public AssinaturaAdminController(AssinaturaPlatformService service) {
        this.service = service;
    }

    @GetMapping("/organizacoes/{id}/assinatura")
    public ResponseEntity<ApiResponseDTO<AssinaturaResponseDTO>> buscarAtiva(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.buscarAtiva(id)));
    }

    @GetMapping("/organizacoes/{id}/assinatura/historico")
    public ResponseEntity<ApiResponseDTO<List<AssinaturaResponseDTO>>> historico(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarHistorico(id)));
    }

    @GetMapping("/organizacoes/{id}/utilizacao")
    public ResponseEntity<ApiResponseDTO<PoliticaPlanoResumoDTO>> utilizacao(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.consultarUtilizacao(id)));
    }

    @PutMapping("/organizacoes/{id}/plano")
    public ResponseEntity<ApiResponseDTO<AssinaturaResponseDTO>> alterarPlano(
            @PathVariable Long id,
            @Valid @RequestBody OrganizacaoPlanoUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Plano da organizacao atualizado",
                service.alterarPlano(id, request.idPlanoAssinatura())));
    }

    @PutMapping("/organizacoes/{id}/assinatura/status")
    public ResponseEntity<ApiResponseDTO<AssinaturaResponseDTO>> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AssinaturaStatusUpdateDTO request) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Status da assinatura atualizado",
                service.atualizarStatus(id, request)));
    }

    @PostMapping("/organizacoes/{id}/assinatura/trial")
    public ResponseEntity<ApiResponseDTO<AssinaturaResponseDTO>> iniciarTrial(
            @PathVariable Long id,
            @RequestParam Long idPlanoAssinatura,
            @RequestParam(defaultValue = "14") int diasTrial) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Trial iniciado",
                service.iniciarTrial(id, idPlanoAssinatura, diasTrial)));
    }

    @GetMapping("/planos-assinatura/{id}/organizacoes")
    public ResponseEntity<ApiResponseDTO<List<AssinaturaResponseDTO>>> listarPorPlano(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.listarPorPlano(id)));
    }
}
