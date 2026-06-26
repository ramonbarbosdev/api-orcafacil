package com.api_orcafacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.ApiResponseDTO;
import com.api_orcafacil.dto.perfil.PerfilRequest;
import com.api_orcafacil.dto.perfil.PerfilResponse;
import com.api_orcafacil.service.PerfilService;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final PerfilService service;

    public PerfilController(PerfilService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PerfilResponse>> obter() {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operacao realizada com sucesso", service.obter()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<Void>> atualizar(@RequestBody PerfilRequest request) {
        service.atualizar(request);
        return ResponseEntity.ok(new ApiResponseDTO<>("Perfil atualizado", null));
    }

    @PostMapping("/foto")
    public ResponseEntity<ApiResponseDTO<String>> uploadFoto(@RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(new ApiResponseDTO<>("Foto atualizada", service.uploadFoto(file)));
    }

    @DeleteMapping("/foto")
    public ResponseEntity<ApiResponseDTO<Void>> removerFoto() {
        service.removerFoto();
        return ResponseEntity.ok(new ApiResponseDTO<>("Foto removida", null));
    }
}
