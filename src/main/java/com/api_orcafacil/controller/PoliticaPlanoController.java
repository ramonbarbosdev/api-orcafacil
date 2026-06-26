package com.api_orcafacil.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.plano.PoliticaPlanoResumoDTO;
import com.api_orcafacil.service.PoliticaPlanoService;

@RestController
@RequestMapping("/politica-plano")
public class PoliticaPlanoController {

    private final ObjectProvider<PoliticaPlanoService> politicaPlanoService;

    public PoliticaPlanoController(ObjectProvider<PoliticaPlanoService> politicaPlanoService) {
        this.politicaPlanoService = politicaPlanoService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<ApiResponseDTO<PoliticaPlanoResumoDTO>> resumo() {
        PoliticaPlanoService service = politicaPlanoService.getIfAvailable();
        if (service == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", null));
        }
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obterResumoAtual()));
    }
}
