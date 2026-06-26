package com.api_orcafacil.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.dto.LoginRequestDTO;
import com.api_orcafacil.dto.LoginResponseDTO;
import com.api_orcafacil.dto.MeResponseDTO;
import com.api_orcafacil.dto.SelecionarOrganizacaoRequestDTO;
import com.api_orcafacil.dto.SelecionarOrganizacaoResponseDTO;
import com.api_orcafacil.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/selecionar-organizacao")
    public ResponseEntity<SelecionarOrganizacaoResponseDTO> selecionarOrganizacao(
            @Valid @RequestBody SelecionarOrganizacaoRequestDTO request) {
        return ResponseEntity.ok(authService.selecionarOrganizacao(request.idOrganizacao()));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> me() {
        return ResponseEntity.ok(authService.me());
    }
}
