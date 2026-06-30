package com.api_orcafacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.dashboard.DashboardResumoDTO;
import com.api_orcafacil.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public ResponseEntity<ApiResponseDTO<DashboardResumoDTO>> resumo() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obterResumo()));
    }
}
