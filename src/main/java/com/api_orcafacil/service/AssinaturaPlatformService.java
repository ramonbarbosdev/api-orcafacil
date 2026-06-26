package com.api_orcafacil.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.AssinaturaStatus;
import com.api_orcafacil.dto.plano.AssinaturaResponseDTO;
import com.api_orcafacil.dto.plano.AssinaturaStatusUpdateDTO;
import com.api_orcafacil.dto.plano.PoliticaPlanoResumoDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralOrganizacaoAssinaturaRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.repository.central.CentralPlanoAssinaturaRepository;
import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoAssinatura;
import com.api_orcafacil.tenant.central.model.CentralPlanoAssinatura;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
@RequiredArgsConstructor
public class AssinaturaPlatformService {

    private final CentralOrganizacaoAssinaturaRepository assinaturaRepository;
    private final CentralOrganizacaoRepository organizacaoRepository;
    private final CentralPlanoAssinaturaRepository planoRepository;
    private final PoliticaPlanoService politicaPlanoService;

    public AssinaturaResponseDTO buscarAtiva(Long idOrganizacao) {
        validarOrganizacao(idOrganizacao);
        CentralOrganizacaoAssinatura assinatura = assinaturaRepository
                .findFirstByIdOrganizacaoAndTpStatusInOrderByDtInicioDesc(
                        idOrganizacao, List.of(AssinaturaStatus.TRIAL, AssinaturaStatus.ATIVA))
                .orElseGet(() -> assinaturaRepository.findByIdOrganizacaoOrderByDtInicioDesc(idOrganizacao).stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Assinatura nao encontrada")));
        return toResponse(assinatura);
    }

    public List<AssinaturaResponseDTO> listarHistorico(Long idOrganizacao) {
        validarOrganizacao(idOrganizacao);
        return assinaturaRepository.findByIdOrganizacaoOrderByDtInicioDesc(idOrganizacao).stream()
                .map(this::toResponse)
                .toList();
    }

    public PoliticaPlanoResumoDTO consultarUtilizacao(Long idOrganizacao) {
        validarOrganizacao(idOrganizacao);
        return politicaPlanoService.obterResumo(idOrganizacao);
    }

    public AssinaturaResponseDTO alterarPlano(Long idOrganizacao, Long idPlanoAssinatura) {
        CentralOrganizacao organizacao = validarOrganizacao(idOrganizacao);
        CentralPlanoAssinatura plano = planoRepository.findById(idPlanoAssinatura)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado"));
        if (!plano.isFlAtivo()) {
            throw new BusinessException("Plano inativo nao pode ser contratado");
        }

        encerrarAssinaturasAtivas(idOrganizacao, AssinaturaStatus.CANCELADA);

        organizacao.setIdPlanoAssinatura(idPlanoAssinatura);
        organizacaoRepository.save(organizacao);

        CentralOrganizacaoAssinatura nova = criarAssinatura(idOrganizacao, idPlanoAssinatura, AssinaturaStatus.ATIVA, null);
        sincronizarStatusOrganizacao(organizacao, nova.getTpStatus());
        return toResponse(nova);
    }

    public AssinaturaResponseDTO atualizarStatus(Long idOrganizacao, AssinaturaStatusUpdateDTO request) {
        CentralOrganizacao organizacao = validarOrganizacao(idOrganizacao);
        CentralOrganizacaoAssinatura assinatura = assinaturaRepository
                .findFirstByIdOrganizacaoAndTpStatusInOrderByDtInicioDesc(
                        idOrganizacao, List.of(AssinaturaStatus.TRIAL, AssinaturaStatus.ATIVA))
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura ativa nao encontrada"));

        assinatura.setTpStatus(request.tpStatus());
        if (request.dtFim() != null) {
            assinatura.setDtFim(request.dtFim());
        }
        if (request.dtFimTrial() != null) {
            assinatura.setDtFimTrial(request.dtFimTrial());
        }
        assinaturaRepository.save(assinatura);
        sincronizarStatusOrganizacao(organizacao, assinatura.getTpStatus());
        return toResponse(assinatura);
    }

    public AssinaturaResponseDTO iniciarTrial(Long idOrganizacao, Long idPlanoAssinatura, int diasTrial) {
        CentralOrganizacao organizacao = validarOrganizacao(idOrganizacao);
        planoRepository.findById(idPlanoAssinatura)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado"));

        encerrarAssinaturasAtivas(idOrganizacao, AssinaturaStatus.CANCELADA);
        organizacao.setIdPlanoAssinatura(idPlanoAssinatura);
        organizacaoRepository.save(organizacao);

        LocalDateTime fimTrial = LocalDateTime.now().plusDays(diasTrial);
        CentralOrganizacaoAssinatura assinatura = criarAssinatura(
                idOrganizacao, idPlanoAssinatura, AssinaturaStatus.TRIAL, fimTrial);
        sincronizarStatusOrganizacao(organizacao, assinatura.getTpStatus());
        return toResponse(assinatura);
    }

    public void criarAssinaturaInicial(Long idOrganizacao, Long idPlanoAssinatura) {
        if (assinaturaRepository.findByIdOrganizacaoOrderByDtInicioDesc(idOrganizacao).isEmpty()) {
            criarAssinatura(idOrganizacao, idPlanoAssinatura, AssinaturaStatus.ATIVA, null);
        }
    }

    public List<AssinaturaResponseDTO> listarPorPlano(Long idPlanoAssinatura) {
        planoRepository.findById(idPlanoAssinatura)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado"));
        return organizacaoRepository.findByIdPlanoAssinatura(idPlanoAssinatura).stream()
                .map(org -> buscarAtiva(org.getIdOrganizacao()))
                .toList();
    }

    private CentralOrganizacaoAssinatura criarAssinatura(
            Long idOrganizacao,
            Long idPlanoAssinatura,
            AssinaturaStatus status,
            LocalDateTime dtFimTrial) {
        CentralOrganizacaoAssinatura assinatura = new CentralOrganizacaoAssinatura();
        assinatura.setIdOrganizacao(idOrganizacao);
        assinatura.setIdPlanoAssinatura(idPlanoAssinatura);
        assinatura.setTpStatus(status);
        assinatura.setDtInicio(LocalDateTime.now());
        assinatura.setDtFimTrial(dtFimTrial);
        assinatura.setDtProximoCiclo(LocalDateTime.now().plusMonths(1));
        assinatura.setFlRenovacaoAutomatica(true);
        return assinaturaRepository.save(assinatura);
    }

    private void encerrarAssinaturasAtivas(Long idOrganizacao, AssinaturaStatus novoStatus) {
        assinaturaRepository.findFirstByIdOrganizacaoAndTpStatusInOrderByDtInicioDesc(
                idOrganizacao, List.of(AssinaturaStatus.TRIAL, AssinaturaStatus.ATIVA))
                .ifPresent(assinatura -> {
                    assinatura.setTpStatus(novoStatus);
                    assinatura.setDtFim(LocalDateTime.now());
                    assinaturaRepository.save(assinatura);
                });
    }

    private void sincronizarStatusOrganizacao(CentralOrganizacao organizacao, AssinaturaStatus statusAssinatura) {
        OrganizationStatus statusOrg = switch (statusAssinatura) {
            case TRIAL, ATIVA -> OrganizationStatus.ATIVA;
            case INADIMPLENTE -> OrganizationStatus.SUSPENSA;
            case CANCELADA, EXPIRADA -> OrganizationStatus.CANCELADA;
        };
        if (organizacao.getStatus() != OrganizationStatus.EM_PROVISIONAMENTO
                && organizacao.getStatus() != OrganizationStatus.PROVISIONAMENTO_FALHOU) {
            organizacao.setStatus(statusOrg);
            organizacaoRepository.save(organizacao);
        }
    }

    private CentralOrganizacao validarOrganizacao(Long idOrganizacao) {
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
    }

    private AssinaturaResponseDTO toResponse(CentralOrganizacaoAssinatura assinatura) {
        String nmPlano = planoRepository.findById(assinatura.getIdPlanoAssinatura())
                .map(CentralPlanoAssinatura::getNmPlanoAssinatura)
                .orElse("—");
        return new AssinaturaResponseDTO(
                assinatura.getIdOrganizacaoAssinatura(),
                assinatura.getIdOrganizacao(),
                assinatura.getIdPlanoAssinatura(),
                nmPlano,
                assinatura.getTpStatus().name(),
                assinatura.getDtInicio(),
                assinatura.getDtFim(),
                assinatura.getDtFimTrial(),
                assinatura.getDtProximoCiclo(),
                assinatura.isFlRenovacaoAutomatica());
    }
}
