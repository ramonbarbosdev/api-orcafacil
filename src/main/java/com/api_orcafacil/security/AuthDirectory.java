package com.api_orcafacil.security;

import java.util.List;
import java.util.Optional;

import com.api_orcafacil.dto.OrganizacaoLoginDTO;

public interface AuthDirectory {

    Optional<AuthDirectoryUser> buscarUsuarioAtivoPorCpf(String nuCpf);

    List<OrganizacaoLoginDTO> listarOrganizacoesAtivas(Long idUsuario);

    Optional<AuthOrganizationMembership> buscarVinculoAtivo(Long idUsuario, Long idOrganizacao);

    List<String> listarPermissoes(Long idUsuario, Long idOrganizacao, String role);
}
