package com.api_orcafacil.service;

import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.perfil.PerfilRequest;
import com.api_orcafacil.dto.perfil.PerfilResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
@Service
public class PerfilService {

    private final TenantContextService tenantContextService;
    private final ObjectProvider<NamedParameterJdbcTemplate> centralJdbc;
    private final PasswordEncoder passwordEncoder;
    private final AnexoService anexoService;

    public PerfilService(TenantContextService tenantContextService,
            ObjectProvider<NamedParameterJdbcTemplate> centralJdbcProvider,
            PasswordEncoder passwordEncoder,
            AnexoService anexoService) {
        this.tenantContextService = tenantContextService;
        this.centralJdbc = centralJdbcProvider;
        this.passwordEncoder = passwordEncoder;
        this.anexoService = anexoService;
    }

    public PerfilResponse obter() {
        Long idUsuario = tenantContextService.idUsuario();
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc == null) {
            throw new ResourceNotFoundException("Perfil nao disponivel sem banco central");
        }
        return jdbc.queryForObject("""
                select id_usuario, nu_cpf, nm_usuario, ds_foto_url
                from usuario_global where id_usuario = :id
                """,
                Map.of("id", idUsuario),
                (rs, rowNum) -> {
                    PerfilResponse p = new PerfilResponse();
                    p.setIdUsuario(rs.getLong("id_usuario"));
                    p.setLogin(rs.getString("nu_cpf"));
                    p.setNome(rs.getString("nm_usuario"));
                    p.setFotoUrl(rs.getString("ds_foto_url"));
                    return p;
                });
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void atualizar(PerfilRequest request) {
        Long idUsuario = tenantContextService.idUsuario();
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc == null) {
            throw new ResourceNotFoundException("Perfil nao disponivel sem banco central");
        }
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            jdbc.update("""
                    update usuario_global set nm_usuario = :nome, ds_senha = :senha, dt_atualizacao = now()
                    where id_usuario = :id
                    """,
                    Map.of("nome", request.getNome(), "senha", passwordEncoder.encode(request.getSenha()), "id", idUsuario));
        } else {
            jdbc.update("""
                    update usuario_global set nm_usuario = :nome, dt_atualizacao = now()
                    where id_usuario = :id
                    """,
                    Map.of("nome", request.getNome(), "id", idUsuario));
        }
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public String uploadFoto(MultipartFile file) throws Exception {
        Long idUsuario = tenantContextService.idUsuario();
        PerfilResponse atual = obter();
        if (atual.getFotoUrl() != null) {
            anexoService.removerFotoPerfil(atual.getFotoUrl());
        }
        String url = anexoService.uploadFotoPerfil(idUsuario, file);
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc != null) {
            jdbc.update(
                    "update usuario_global set ds_foto_url = :url, dt_atualizacao = now() where id_usuario = :id",
                    Map.of("url", url, "id", idUsuario));
        }
        return url;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void removerFoto() {
        Long idUsuario = tenantContextService.idUsuario();
        PerfilResponse atual = obter();
        anexoService.removerFotoPerfil(atual.getFotoUrl());
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc != null) {
            jdbc.update(
                    "update usuario_global set ds_foto_url = null, dt_atualizacao = now() where id_usuario = :id",
                    Map.of("id", idUsuario));
        }
    }
}
