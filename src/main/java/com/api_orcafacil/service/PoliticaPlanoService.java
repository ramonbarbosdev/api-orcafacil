package com.api_orcafacil.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.AssinaturaStatus;
import com.api_orcafacil.common.ChaveLimite;
import com.api_orcafacil.dto.plano.LimitePlanoDTO;
import com.api_orcafacil.dto.plano.PoliticaPlanoResumoDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.repository.ClienteRepository;
import com.api_orcafacil.repository.ServicoRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoAssinaturaRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoConsumoRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.repository.central.CentralPlanoAssinaturaRepository;
import com.api_orcafacil.repository.central.CentralPlanoLimiteRepository;
import com.api_orcafacil.repository.central.CentralPlanoPermissaoRepository;
import com.api_orcafacil.repository.central.CentralTipoLimiteRepository;
import com.api_orcafacil.repository.central.CentralUsuarioOrganizacaoRepository;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoAssinatura;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoConsumo;
import com.api_orcafacil.tenant.central.model.CentralPlanoAssinatura;
import com.api_orcafacil.tenant.central.model.CentralPlanoLimite;
import com.api_orcafacil.tenant.central.model.CentralTipoLimite;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class PoliticaPlanoService {

    private static final List<AssinaturaStatus> STATUS_ACESSO = List.of(
            AssinaturaStatus.TRIAL, AssinaturaStatus.ATIVA);

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final CentralOrganizacaoAssinaturaRepository assinaturaRepository;
    private final CentralPlanoAssinaturaRepository planoRepository;
    private final CentralPlanoLimiteRepository planoLimiteRepository;
    private final CentralPlanoPermissaoRepository planoPermissaoRepository;
    private final CentralTipoLimiteRepository tipoLimiteRepository;
    private final CentralOrganizacaoConsumoRepository consumoRepository;
    private final CentralUsuarioOrganizacaoRepository usuarioOrganizacaoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final TenantContextService tenantContextService;

    public PoliticaPlanoService(
            CentralOrganizacaoRepository organizacaoRepository,
            CentralOrganizacaoAssinaturaRepository assinaturaRepository,
            CentralPlanoAssinaturaRepository planoRepository,
            CentralPlanoLimiteRepository planoLimiteRepository,
            CentralPlanoPermissaoRepository planoPermissaoRepository,
            CentralTipoLimiteRepository tipoLimiteRepository,
            CentralOrganizacaoConsumoRepository consumoRepository,
            CentralUsuarioOrganizacaoRepository usuarioOrganizacaoRepository,
            ClienteRepository clienteRepository,
            ServicoRepository servicoRepository,
            TenantContextService tenantContextService) {
        this.organizacaoRepository = organizacaoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.planoRepository = planoRepository;
        this.planoLimiteRepository = planoLimiteRepository;
        this.planoPermissaoRepository = planoPermissaoRepository;
        this.tipoLimiteRepository = tipoLimiteRepository;
        this.consumoRepository = consumoRepository;
        this.usuarioOrganizacaoRepository = usuarioOrganizacaoRepository;
        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public boolean assinaturaPermiteAcesso(Long idOrganizacao) {
        return resolverAssinaturaAtiva(idOrganizacao).isPresent();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public void validarAcessoOrganizacao(Long idOrganizacao) {
        if (!assinaturaPermiteAcesso(idOrganizacao)) {
            throw new BusinessException(
                    "Assinatura inativa ou expirada. Entre em contato com o suporte para regularizar o plano.");
        }
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public void validarRecurso(Long idOrganizacao, String chaveRecurso) {
        validarAcessoOrganizacao(idOrganizacao);
        Long idPlano = obterIdPlano(idOrganizacao);
        List<String> recursosPlano = planoPermissaoRepository.findChavesByIdPlanoAssinatura(idPlano);
        if (!recursosPlano.contains(chaveRecurso)) {
            throw new BusinessException("Recurso nao disponivel no plano contratado: " + chaveRecurso);
        }
    }

    public void validarRecursoAtual(String chaveRecurso) {
        validarRecurso(tenantContextService.idOrganizacaoObrigatoria(), chaveRecurso);
    }

    public void validarLimiteAtual(String chaveLimite) {
        validarLimite(tenantContextService.idOrganizacaoObrigatoria(), chaveLimite);
    }

    public void validarLimiteNovoRegistroAtual(String chaveLimite) {
        validarLimiteNovoRegistro(tenantContextService.idOrganizacaoObrigatoria(), chaveLimite);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public void validarLimite(Long idOrganizacao, String chaveLimite) {
        validarAcessoOrganizacao(idOrganizacao);
        Long limite = obterLimitePlano(idOrganizacao, chaveLimite);
        if (limite == null) {
            return;
        }
        long consumo = obterConsumo(idOrganizacao, chaveLimite);
        if (consumo >= limite) {
            throw new BusinessException("Limite do plano atingido para: " + chaveLimite);
        }
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void validarLimiteNovoRegistro(Long idOrganizacao, String chaveLimite) {
        validarAcessoOrganizacao(idOrganizacao);
        Long limite = obterLimitePlano(idOrganizacao, chaveLimite);
        if (limite == null) {
            return;
        }
        if (isConsumoPorPeriodo(chaveLimite)) {
            long consumo = obterConsumoComLock(idOrganizacao, chaveLimite);
            if (consumo + 1 > limite) {
                throw new BusinessException("Limite do plano atingido para: " + chaveLimite);
            }
            return;
        }
        long consumo = obterConsumo(idOrganizacao, chaveLimite);
        if (consumo + 1 > limite) {
            throw new BusinessException("Limite do plano atingido para: " + chaveLimite);
        }
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void registrarConsumo(Long idOrganizacao, String chaveLimite, long incremento) {
        if (incremento == 0) {
            return;
        }
        LocalDate referencia = referenciaConsumo(chaveLimite);
        CentralOrganizacaoConsumo consumo = consumoRepository
                .findForUpdate(idOrganizacao, chaveLimite, referencia)
                .orElseGet(() -> {
                    CentralOrganizacaoConsumo novo = new CentralOrganizacaoConsumo();
                    novo.setIdOrganizacao(idOrganizacao);
                    novo.setNmChaveLimite(chaveLimite);
                    novo.setDtReferencia(referencia);
                    novo.setNuConsumo(0L);
                    return novo;
                });
        consumo.setNuConsumo(Math.max(0L, consumo.getNuConsumo() + incremento));
        consumoRepository.save(consumo);
    }

    public void registrarConsumoAtual(String chaveLimite, long incremento) {
        registrarConsumo(tenantContextService.idOrganizacaoObrigatoria(), chaveLimite, incremento);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public long obterConsumo(Long idOrganizacao, String chaveLimite) {
        Optional<CentralOrganizacaoConsumo> persistido = consumoRepository
                .findByIdOrganizacaoAndNmChaveLimiteAndDtReferencia(
                        idOrganizacao, chaveLimite, referenciaConsumo(chaveLimite));
        if (persistido.isPresent()) {
            return persistido.get().getNuConsumo();
        }
        if (isConsumoPorPeriodo(chaveLimite)) {
            return 0L;
        }
        return calcularConsumoDinamico(idOrganizacao, chaveLimite);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public Long obterLimitePlano(Long idOrganizacao, String chaveLimite) {
        Long idPlano = obterIdPlano(idOrganizacao);
        return planoLimiteRepository.findByIdPlanoAssinatura(idPlano).stream()
                .filter(limite -> chaveLimite.equals(limite.getNmChaveLimite()))
                .map(CentralPlanoLimite::getNuValor)
                .findFirst()
                .orElse(null);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public PoliticaPlanoResumoDTO obterResumo(Long idOrganizacao) {
        CentralOrganizacao organizacao = organizacaoRepository.findById(idOrganizacao).orElse(null);
        if (organizacao == null) {
            return null;
        }
        Long idPlano = organizacao.getIdPlanoAssinatura();
        String nmPlano = planoRepository.findById(idPlano)
                .map(CentralPlanoAssinatura::getNmPlanoAssinatura)
                .orElse("-");
        Optional<CentralOrganizacaoAssinatura> assinatura = resolverAssinaturaAtiva(idOrganizacao);
        String status = assinatura.map(a -> a.getTpStatus().name()).orElse("INDEFINIDA");
        boolean ativa = assinatura.isPresent();

        List<LimitePlanoDTO> limites = new ArrayList<>();
        if (idPlano != null) {
            for (CentralPlanoLimite limite : planoLimiteRepository.findByIdPlanoAssinatura(idPlano)) {
                String nmLimite = tipoLimiteRepository.findAll().stream()
                        .filter(t -> t.getNmChave().equals(limite.getNmChaveLimite()))
                        .map(CentralTipoLimite::getNmLimite)
                        .findFirst()
                        .orElse(limite.getNmChaveLimite());
                long consumo = obterConsumo(idOrganizacao, limite.getNmChaveLimite());
                limites.add(new LimitePlanoDTO(
                        limite.getNmChaveLimite(), nmLimite, limite.getNuValor(), consumo));
            }
        }
        return new PoliticaPlanoResumoDTO(idPlano, nmPlano, status, ativa, limites);
    }

    public PoliticaPlanoResumoDTO obterResumoAtual() {
        return obterResumo(tenantContextService.idOrganizacaoObrigatoria());
    }

    private Optional<CentralOrganizacaoAssinatura> resolverAssinaturaAtiva(Long idOrganizacao) {
        Optional<CentralOrganizacaoAssinatura> assinatura = assinaturaRepository
                .findFirstByIdOrganizacaoAndTpStatusInOrderByDtInicioDesc(idOrganizacao, STATUS_ACESSO);
        if (assinatura.isEmpty()) {
            return Optional.empty();
        }
        CentralOrganizacaoAssinatura atual = assinatura.get();
        LocalDateTime agora = LocalDateTime.now();
        if (atual.getTpStatus() == AssinaturaStatus.TRIAL
                && atual.getDtFimTrial() != null
                && atual.getDtFimTrial().isBefore(agora)) {
            return Optional.empty();
        }
        if (atual.getDtFim() != null && atual.getDtFim().isBefore(agora)) {
            return Optional.empty();
        }
        return assinatura;
    }

    private Long obterIdPlano(Long idOrganizacao) {
        return organizacaoRepository.findById(idOrganizacao)
                .map(CentralOrganizacao::getIdPlanoAssinatura)
                .orElse(1L);
    }

    private boolean isConsumoPorPeriodo(String chaveLimite) {
        return ChaveLimite.ORCAMENTOS_MES.equals(chaveLimite);
    }

    private long calcularConsumoDinamico(Long idOrganizacao, String chaveLimite) {
        return switch (chaveLimite) {
            case ChaveLimite.CLIENTES -> clienteRepository.countByIdOrganizacao(idOrganizacao);
            case ChaveLimite.SERVICOS -> servicoRepository.countByIdOrganizacao(idOrganizacao);
            case ChaveLimite.USUARIOS -> usuarioOrganizacaoRepository.countByIdOrganizacaoAndFlAtivoTrue(idOrganizacao);
            default -> 0L;
        };
    }

    private LocalDate referenciaConsumo(String chaveLimite) {
        if (ChaveLimite.ORCAMENTOS_MES.equals(chaveLimite)) {
            YearMonth mes = YearMonth.now();
            return mes.atDay(1);
        }
        return LocalDate.EPOCH;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public long obterConsumoComLock(Long idOrganizacao, String chaveLimite) {
        LocalDate referencia = referenciaConsumo(chaveLimite);
        return consumoRepository.findForUpdate(idOrganizacao, chaveLimite, referencia)
                .map(CentralOrganizacaoConsumo::getNuConsumo)
                .orElse(0L);
    }
}
