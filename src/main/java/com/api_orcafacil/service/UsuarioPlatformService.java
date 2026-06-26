package com.api_orcafacil.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.UsuarioBuscaDTO;
import com.api_orcafacil.repository.central.CentralUsuarioGlobalRepository;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class UsuarioPlatformService {

    private final CentralUsuarioGlobalRepository usuarioGlobalRepository;

    public UsuarioBuscaDTO buscarPorCpf(String nuCpf) {
        String cpf = normalizarCpf(nuCpf);
        if (cpf.length() != 11) {
            return UsuarioBuscaDTO.naoEncontrado();
        }
        return usuarioGlobalRepository.findByNuCpf(cpf)
                .filter(usuario -> usuario.isFlAtivo())
                .map(usuario -> new UsuarioBuscaDTO(
                        true,
                        usuario.getIdUsuario(),
                        usuario.getNuCpf(),
                        usuario.getNmUsuario(),
                        usuario.getTpGlobal().name()))
                .orElseGet(UsuarioBuscaDTO::naoEncontrado);
    }

    private String normalizarCpf(String nuCpf) {
        if (nuCpf == null) {
            return "";
        }
        return nuCpf.replaceAll("\\D", "");
    }
}
