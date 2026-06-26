package com.api_orcafacil.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.TipoGlobal;
import com.api_orcafacil.dto.LoginRequestDTO;
import com.api_orcafacil.dto.LoginResponseDTO;
import com.api_orcafacil.dto.MeResponseDTO;
import com.api_orcafacil.dto.OrganizacaoLoginDTO;
import com.api_orcafacil.dto.SelecionarOrganizacaoResponseDTO;
import com.api_orcafacil.exception.UnauthorizedException;
import com.api_orcafacil.security.AuthDirectory;
import com.api_orcafacil.security.AuthDirectoryUser;
import com.api_orcafacil.security.AuthOrganizationMembership;
import com.api_orcafacil.security.JwtAuthentication;
import com.api_orcafacil.security.JwtService;
import com.api_orcafacil.tenant.OrganizationResolver;

@Service
public class AuthService {

    private static final String CREDENCIAIS_INVALIDAS = "CPF ou senha invalidos";

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantContextService tenantContextService;
    private final AuthDirectory authDirectory;
    private final OrganizationResolver organizationResolver;

    public AuthService(
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TenantContextService tenantContextService,
            AuthDirectory authDirectory,
            OrganizationResolver organizationResolver) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantContextService = tenantContextService;
        this.authDirectory = authDirectory;
        this.organizationResolver = organizationResolver;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        AuthDirectoryUser usuario = authDirectory.buscarUsuarioAtivoPorCpf(request.nuCpf())
                .filter(u -> passwordEncoder.matches(request.dsSenha(), u.dsSenha()))
                .orElseThrow(() -> new UnauthorizedException(CREDENCIAIS_INVALIDAS));

        String token = jwtService.gerarTokenSemTenant(usuario.idUsuario(), usuario.tipoGlobal());

        if (TipoGlobal.SUPER_ADMIN.name().equals(usuario.tipoGlobal())) {
            return new LoginResponseDTO(token, "SUPER_ADMIN", false, List.of());
        }

        List<OrganizacaoLoginDTO> organizacoes = authDirectory.listarOrganizacoesAtivas(usuario.idUsuario());
        return new LoginResponseDTO(token, "DEFAULT", true, organizacoes);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public SelecionarOrganizacaoResponseDTO selecionarOrganizacao(Long idOrganizacao) {
        JwtAuthentication atual = tenantContextService.atual();
        if (!"DEFAULT".equals(atual.getTipoGlobal())) {
            throw new UnauthorizedException("SUPER_ADMIN nao seleciona organizacao");
        }

        AuthOrganizationMembership vinculo = authDirectory
                .buscarVinculoAtivo(atual.getIdUsuario(), idOrganizacao)
                .orElseThrow(() -> new UnauthorizedException("Usuario sem vinculo ativo com a organizacao"));

        try {
            organizationResolver.resolver(idOrganizacao);
        } catch (RuntimeException ex) {
            throw new UnauthorizedException("Organizacao indisponivel ou inativa");
        }

        List<String> permissoes = authDirectory.listarPermissoes(
                atual.getIdUsuario(), idOrganizacao, vinculo.dsRole());

        String token = jwtService.gerarTokenComTenant(
                atual.getIdUsuario(), idOrganizacao, vinculo.dsRole(), permissoes);

        return new SelecionarOrganizacaoResponseDTO(token, idOrganizacao, vinculo.dsRole(), permissoes);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public MeResponseDTO me() {
        JwtAuthentication atual = tenantContextService.atual();
        return new MeResponseDTO(
                atual.getIdUsuario(),
                atual.getTipoGlobal(),
                atual.getIdOrganizacao(),
                atual.getRole(),
                atual.getPermissoes());
    }
}
