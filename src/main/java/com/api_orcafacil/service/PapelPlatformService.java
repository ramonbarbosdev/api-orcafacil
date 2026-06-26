package com.api_orcafacil.service;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.PapelDetalheDTO;
import com.api_orcafacil.dto.PapelResponseDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralPapelPermissaoPadraoRepository;
import com.api_orcafacil.repository.central.CentralPapelRepository;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
@RequiredArgsConstructor
public class PapelPlatformService {

    private final CentralPapelRepository papelRepository;
    private final CentralPapelPermissaoPadraoRepository papelPermissaoPadraoRepository;
    private final PermissaoPlatformService permissaoPlatformService;

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<PapelResponseDTO> listar() {
        return papelRepository.findByFlAtivoTrueOrderByNmPapelAsc().stream()
                .map(papel -> new PapelResponseDTO(
                        papel.getIdPapel(),
                        papel.getNmPapel(),
                        papel.isFlAtivo(),
                        (int) papelPermissaoPadraoRepository.countByIdPapel(papel.getIdPapel())))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public PapelDetalheDTO buscar(Long idPapel) {
        return papelRepository.findByIdPapelAndFlAtivoTrue(idPapel)
                .map(papel -> new PapelDetalheDTO(
                        papel.getIdPapel(),
                        papel.getNmPapel(),
                        papelPermissaoPadraoRepository.findChavesByIdPapel(idPapel)))
                .orElseThrow(() -> new ResourceNotFoundException("Papel nao encontrado"));
    }

    public PapelDetalheDTO atualizarPermissoes(Long idPapel, List<String> chaves) {
        buscar(idPapel);
        permissaoPlatformService.substituirPermissoesPapel(idPapel, chaves);
        return buscar(idPapel);
    }
}
