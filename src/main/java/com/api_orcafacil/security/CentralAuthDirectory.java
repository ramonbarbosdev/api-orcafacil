package com.api_orcafacil.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api_orcafacil.dto.OrganizacaoLoginDTO;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.repository.central.CentralPapelPermissaoPadraoRepository;
import com.api_orcafacil.repository.central.CentralPermissaoGlobalRepository;
import com.api_orcafacil.repository.central.CentralPlanoPermissaoRepository;
import com.api_orcafacil.repository.central.CentralUsuarioGlobalRepository;
import com.api_orcafacil.repository.central.CentralUsuarioOrganizacaoRepository;
import com.api_orcafacil.repository.central.CentralUsuarioPermissaoRepository;
import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.central.model.CentralUsuarioOrganizacao;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@RequiredArgsConstructor
public class CentralAuthDirectory implements AuthDirectory {

    private final CentralUsuarioGlobalRepository usuarioGlobalRepository;
    private final CentralUsuarioOrganizacaoRepository usuarioOrganizacaoRepository;
    private final CentralOrganizacaoRepository organizacaoRepository;
    private final CentralPapelPermissaoPadraoRepository papelPermissaoPadraoRepository;
    private final CentralPlanoPermissaoRepository planoPermissaoRepository;
    private final CentralPermissaoGlobalRepository permissaoRepository;
    private final CentralUsuarioPermissaoRepository usuarioPermissaoRepository;

    @Override
    public Optional<AuthDirectoryUser> buscarUsuarioAtivoPorCpf(String nuCpf) {
        return usuarioGlobalRepository.findByNuCpfAndFlAtivoTrue(nuCpf)
                .map(usuario -> new AuthDirectoryUser(
                        usuario.getIdUsuario(),
                        usuario.getNuCpf(),
                        usuario.getNmUsuario(),
                        usuario.getNmEmail(),
                        usuario.getDsSenha(),
                        usuario.getTpGlobal().name(),
                        usuario.isFlAtivo()));
    }

    @Override
    public List<OrganizacaoLoginDTO> listarOrganizacoesAtivas(Long idUsuario) {
        return usuarioOrganizacaoRepository
                .findOrganizacoesAtivasPorUsuario(idUsuario, OrganizationStatus.ATIVA)
                .stream()
                .map(this::toOrganizacaoLogin)
                .toList();
    }

    @Override
    public Optional<AuthOrganizationMembership> buscarVinculoAtivo(Long idUsuario, Long idOrganizacao) {
        return usuarioOrganizacaoRepository
                .findVinculoAtivo(idUsuario, idOrganizacao, OrganizationStatus.ATIVA)
                .map(vinculo -> new AuthOrganizationMembership(
                        vinculo.getIdUsuarioOrganizacao(),
                        vinculo.getIdUsuario(),
                        vinculo.getOrganizacao().getIdOrganizacao(),
                        vinculo.getOrganizacao().getNmOrganizacao(),
                        vinculo.getDsRole()));
    }

    @Override
    public List<String> listarPermissoes(Long idUsuario, Long idOrganizacao, String role) {
        LinkedHashSet<String> tetoPlano = new LinkedHashSet<>(permissoesPorPlano(idOrganizacao));
        LinkedHashSet<String> permissoes = new LinkedHashSet<>();

        for (String chave : permissoesPorPapelPadrao(role)) {
            if (tetoPlano.contains(chave)) {
                permissoes.add(chave);
            }
        }
        for (String chave : permissoesPorUsuario(idUsuario, idOrganizacao)) {
            if (tetoPlano.contains(chave)) {
                permissoes.add(chave);
            }
        }
        return List.copyOf(permissoes);
    }

    private OrganizacaoLoginDTO toOrganizacaoLogin(CentralUsuarioOrganizacao vinculo) {
        return new OrganizacaoLoginDTO(
                vinculo.getOrganizacao().getIdOrganizacao(),
                vinculo.getOrganizacao().getNmOrganizacao(),
                vinculo.getDsRole());
    }

    private List<String> permissoesPorPapelPadrao(String role) {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return papelPermissaoPadraoRepository.findChavesByNmPapel(role);
    }

    private List<String> permissoesPorPlano(Long idOrganizacao) {
        Long idPlano = organizacaoRepository.findById(idOrganizacao)
                .map(org -> org.getIdPlanoAssinatura())
                .orElse(null);

        if (idPlano == null) {
            return listarTodasPermissoesAtivas();
        }

        List<String> chaves = planoPermissaoRepository.findChavesByIdPlanoAssinatura(idPlano);
        return chaves.isEmpty() ? listarTodasPermissoesAtivas() : chaves;
    }

    private List<String> listarTodasPermissoesAtivas() {
        return permissaoRepository.findByFlAtivoTrueOrderByNmChaveAsc().stream()
                .map(permissao -> permissao.getNmChave())
                .toList();
    }

    private List<String> permissoesPorUsuario(Long idUsuario, Long idOrganizacao) {
        return usuarioPermissaoRepository.findChavesByUsuarioEOrganizacao(idUsuario, idOrganizacao);
    }
}
