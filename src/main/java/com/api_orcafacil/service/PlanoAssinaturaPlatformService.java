package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.PlanoAssinaturaRequest;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralPlanoAssinaturaRepository;
import com.api_orcafacil.repository.central.CentralPlanoPermissaoRepository;
import com.api_orcafacil.dto.plano.PlanoLimiteItemDTO;
import com.api_orcafacil.dto.plano.PlanoLimitesUpdateDTO;
import com.api_orcafacil.dto.plano.TipoLimiteResponseDTO;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.repository.central.CentralPlanoLimiteRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoAssinaturaRepository;
import com.api_orcafacil.repository.central.CentralTipoLimiteRepository;
import com.api_orcafacil.tenant.central.model.CentralPlanoLimite;
import com.api_orcafacil.tenant.central.model.CentralTipoLimite;
import com.api_orcafacil.tenant.central.CentralPlanoAssinaturaMapper;
import com.api_orcafacil.tenant.central.model.CentralPlanoAssinatura;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
@RequiredArgsConstructor
public class PlanoAssinaturaPlatformService {

    private final CentralPlanoAssinaturaRepository planoRepository;
    private final CentralPlanoPermissaoRepository planoPermissaoRepository;
    private final CentralPlanoLimiteRepository planoLimiteRepository;
    private final CentralTipoLimiteRepository tipoLimiteRepository;
    private final CentralOrganizacaoAssinaturaRepository assinaturaRepository;
    private final PermissaoPlatformService permissaoPlatformService;

    public List<PlanoAssinaturaResponse> listar() {
        return planoRepository.findAllByOrderByNmPlanoAssinaturaAsc().stream()
                .map(CentralPlanoAssinaturaMapper::toResponse)
                .toList();
    }

    public PlanoAssinaturaResponse buscar(Long id) {
        return CentralPlanoAssinaturaMapper.toResponse(buscarEntidade(id));
    }

    public PlanoAssinaturaResponse criar(PlanoAssinaturaRequest request) {
        CentralPlanoAssinatura plano = new CentralPlanoAssinatura();
        plano.setNmPlanoAssinatura(request.getNmPlanoAssinatura());
        plano.setVlMensal(request.getVlMensal() != null ? BigDecimal.valueOf(request.getVlMensal()) : null);
        plano.setNuLimiteMensagens(request.getNuLimiteMensagens() != null ? request.getNuLimiteMensagens() : 0);
        plano.setNuLimiteAtendentes(request.getNuLimiteAtendentes() != null ? request.getNuLimiteAtendentes() : 0);
        plano.setFlAtivo(request.getFlAtivo() != null ? request.getFlAtivo() : true);

        CentralPlanoAssinatura salvo = planoRepository.save(plano);
        permissaoPlatformService.concederTodasPermissoesPlano(salvo.getIdPlanoAssinatura());
        return CentralPlanoAssinaturaMapper.toResponse(salvo);
    }

    public PlanoAssinaturaResponse atualizar(Long id, PlanoAssinaturaRequest request) {
        CentralPlanoAssinatura plano = buscarEntidade(id);
        plano.setNmPlanoAssinatura(request.getNmPlanoAssinatura());
        if (request.getVlMensal() != null) {
            plano.setVlMensal(BigDecimal.valueOf(request.getVlMensal()));
        }
        if (request.getNuLimiteMensagens() != null) {
            plano.setNuLimiteMensagens(request.getNuLimiteMensagens());
        }
        if (request.getNuLimiteAtendentes() != null) {
            plano.setNuLimiteAtendentes(request.getNuLimiteAtendentes());
        }
        if (request.getFlAtivo() != null) {
            plano.setFlAtivo(request.getFlAtivo());
        }
        return CentralPlanoAssinaturaMapper.toResponse(planoRepository.save(plano));
    }

    public void excluir(Long id) {
        buscarEntidade(id);
        if (assinaturaRepository.countByIdPlanoAssinatura(id) > 0) {
            throw new ConflictException("Plano em uso por organizacoes e nao pode ser excluido");
        }
        planoPermissaoRepository.deleteByIdPlanoAssinatura(id);
        planoLimiteRepository.deleteByIdPlanoAssinatura(id);
        planoRepository.deleteById(id);
    }

    public List<TipoLimiteResponseDTO> listarTiposLimite() {
        return tipoLimiteRepository.findByFlAtivoTrueOrderByNmLimiteAsc().stream()
                .map(t -> new TipoLimiteResponseDTO(
                        t.getNmChave(), t.getNmLimite(), t.getDsLimite(), t.getTpLimite()))
                .toList();
    }

    public List<PlanoLimiteItemDTO> listarLimites(Long id) {
        buscarEntidade(id);
        return planoLimiteRepository.findByIdPlanoAssinatura(id).stream()
                .map(l -> new PlanoLimiteItemDTO(l.getNmChaveLimite(), l.getNuValor()))
                .toList();
    }

    public List<PlanoLimiteItemDTO> atualizarLimites(Long id, PlanoLimitesUpdateDTO request) {
        buscarEntidade(id);
        planoLimiteRepository.deleteByIdPlanoAssinatura(id);
        if (request.limites() != null) {
            for (PlanoLimiteItemDTO item : request.limites()) {
                validarTipoLimite(item.nmChaveLimite());
                CentralPlanoLimite limite = new CentralPlanoLimite();
                limite.setIdPlanoAssinatura(id);
                limite.setNmChaveLimite(item.nmChaveLimite());
                limite.setNuValor(item.nuValor());
                planoLimiteRepository.save(limite);
            }
        }
        return listarLimites(id);
    }

    private void validarTipoLimite(String nmChave) {
        tipoLimiteRepository.findAll().stream()
                .map(CentralTipoLimite::getNmChave)
                .filter(nmChave::equals)
                .findFirst()
                .orElseThrow(() -> new ConflictException("Tipo de limite invalido: " + nmChave));
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<String> listarPermissoes(Long id) {
        buscarEntidade(id);
        return planoPermissaoRepository.findChavesByIdPlanoAssinatura(id);
    }

    public List<String> atualizarPermissoes(Long id, List<String> chaves) {
        buscarEntidade(id);
        permissaoPlatformService.substituirPermissoesPlano(id, chaves);
        return listarPermissoes(id);
    }

    private CentralPlanoAssinatura buscarEntidade(Long id) {
        return planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado"));
    }
}
