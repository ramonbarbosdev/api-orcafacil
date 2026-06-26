package com.api_orcafacil.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.perfil.PerfilRequest;
import com.api_orcafacil.dto.perfil.PerfilResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralUsuarioGlobalRepository;
import com.api_orcafacil.tenant.central.model.CentralUsuarioGlobal;

@Service
public class PerfilService {

    private final TenantContextService tenantContextService;
    private final ObjectProvider<CentralUsuarioGlobalRepository> usuarioGlobalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AnexoService anexoService;

    public PerfilService(
            TenantContextService tenantContextService,
            ObjectProvider<CentralUsuarioGlobalRepository> usuarioGlobalRepository,
            PasswordEncoder passwordEncoder,
            AnexoService anexoService) {
        this.tenantContextService = tenantContextService;
        this.usuarioGlobalRepository = usuarioGlobalRepository;
        this.passwordEncoder = passwordEncoder;
        this.anexoService = anexoService;
    }

    public PerfilResponse obter() {
        CentralUsuarioGlobal usuario = buscarUsuarioAtual();
        PerfilResponse perfil = new PerfilResponse();
        perfil.setIdUsuario(usuario.getIdUsuario());
        perfil.setLogin(usuario.getNuCpf());
        perfil.setNome(usuario.getNmUsuario());
        perfil.setFotoUrl(usuario.getDsFotoUrl());
        return perfil;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void atualizar(PerfilRequest request) {
        CentralUsuarioGlobal usuario = buscarUsuarioAtual();
        usuario.setNmUsuario(request.getNome());
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setDsSenha(passwordEncoder.encode(request.getSenha()));
        }
        repositorio().save(usuario);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public String uploadFoto(MultipartFile file) throws Exception {
        CentralUsuarioGlobal usuario = buscarUsuarioAtual();
        if (usuario.getDsFotoUrl() != null) {
            anexoService.removerFotoPerfil(usuario.getDsFotoUrl());
        }
        String url = anexoService.uploadFotoPerfil(usuario.getIdUsuario(), file);
        usuario.setDsFotoUrl(url);
        repositorio().save(usuario);
        return url;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void removerFoto() {
        CentralUsuarioGlobal usuario = buscarUsuarioAtual();
        anexoService.removerFotoPerfil(usuario.getDsFotoUrl());
        usuario.setDsFotoUrl(null);
        repositorio().save(usuario);
    }

    private CentralUsuarioGlobal buscarUsuarioAtual() {
        Long idUsuario = tenantContextService.idUsuario();
        return repositorio().findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    private CentralUsuarioGlobalRepository repositorio() {
        CentralUsuarioGlobalRepository repository = usuarioGlobalRepository.getIfAvailable();
        if (repository == null) {
            throw new ResourceNotFoundException("Perfil nao disponivel sem banco central");
        }
        return repository;
    }
}
