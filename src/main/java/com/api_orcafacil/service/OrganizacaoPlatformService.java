package com.api_orcafacil.service;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.api_orcafacil.common.SlugUtil;
import com.api_orcafacil.common.TipoGlobal;
import com.api_orcafacil.dto.OrganizacaoRequestDTO;
import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioRequestDTO;
import com.api_orcafacil.dto.VinculoUsuarioResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioUpdateDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.provisioning.TenantProvisioningPlan;
import com.api_orcafacil.provisioning.TenantProvisioningService;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.repository.central.CentralUsuarioGlobalRepository;
import com.api_orcafacil.repository.central.CentralUsuarioOrganizacaoRepository;
import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.StorageMode;
import com.api_orcafacil.tenant.central.CentralOrganizacaoMapper;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;
import com.api_orcafacil.tenant.central.model.CentralUsuarioGlobal;
import com.api_orcafacil.tenant.central.model.CentralUsuarioOrganizacao;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class OrganizacaoPlatformService {

    private static final long PLANO_GRATUITO_ID = 1L;

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final CentralUsuarioGlobalRepository usuarioGlobalRepository;
    private final CentralUsuarioOrganizacaoRepository usuarioOrganizacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<TenantProvisioningService> tenantProvisioningService;
    private final boolean autoProvisioningEnabled;

    public OrganizacaoPlatformService(
            CentralOrganizacaoRepository organizacaoRepository,
            CentralUsuarioGlobalRepository usuarioGlobalRepository,
            CentralUsuarioOrganizacaoRepository usuarioOrganizacaoRepository,
            PasswordEncoder passwordEncoder,
            ObjectProvider<TenantProvisioningService> tenantProvisioningService,
            @org.springframework.beans.factory.annotation.Value("${app.saas.provisioning.auto-enabled:true}") boolean autoProvisioningEnabled) {
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioGlobalRepository = usuarioGlobalRepository;
        this.usuarioOrganizacaoRepository = usuarioOrganizacaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantProvisioningService = tenantProvisioningService;
        this.autoProvisioningEnabled = autoProvisioningEnabled;
    }

    public List<OrganizacaoResponseDTO> listar() {
        return organizacaoRepository.findAll().stream()
                .sorted((a, b) -> a.getNmOrganizacao().compareToIgnoreCase(b.getNmOrganizacao()))
                .map(CentralOrganizacaoMapper::toResponse)
                .toList();
    }

    public OrganizacaoResponseDTO buscar(Long idOrganizacao) {
        return CentralOrganizacaoMapper.toResponse(buscarEntidade(idOrganizacao));
    }

    public OrganizacaoResponseDTO criar(OrganizacaoRequestDTO request) {
        String slug = SlugUtil.fromNome(request.nmOrganizacao());
        String databaseName = SlugUtil.databaseName(slug);

        if (organizacaoRepository.existsBySlugOrDatabaseName(slug, databaseName)) {
            throw new ConflictException("Ja existe organizacao com este nome");
        }

        CentralOrganizacao organizacao = new CentralOrganizacao();
        organizacao.setSlug(slug);
        organizacao.setNmOrganizacao(request.nmOrganizacao());
        organizacao.setDsDocumento(request.dsDocumento());
        organizacao.setStatus(OrganizationStatus.EM_PROVISIONAMENTO);
        organizacao.setDatabaseName(databaseName);
        organizacao.setStorageMode(StorageMode.DATABASE_PER_ORG);
        organizacao.setIdPlanoAssinatura(PLANO_GRATUITO_ID);
        organizacao.setFlAtivo(true);

        CentralOrganizacao salva = organizacaoRepository.save(organizacao);
        agendarProvisionamento(salva.getIdOrganizacao(), slug, databaseName);
        return CentralOrganizacaoMapper.toResponse(salva);
    }

    public OrganizacaoResponseDTO atualizar(Long idOrganizacao, OrganizacaoRequestDTO request) {
        CentralOrganizacao organizacao = buscarEntidade(idOrganizacao);
        organizacao.setNmOrganizacao(request.nmOrganizacao());
        organizacao.setDsDocumento(request.dsDocumento());
        return CentralOrganizacaoMapper.toResponse(organizacaoRepository.save(organizacao));
    }

    public void excluir(Long idOrganizacao) {
        CentralOrganizacao organizacao = buscarEntidade(idOrganizacao);
        organizacao.setFlAtivo(false);
        organizacaoRepository.save(organizacao);
    }

    public List<VinculoUsuarioResponseDTO> listarVinculos(Long idOrganizacao) {
        buscarEntidade(idOrganizacao);
        return usuarioOrganizacaoRepository.findVinculosAtivos(idOrganizacao).stream()
                .map(vinculo -> new VinculoUsuarioResponseDTO(
                        vinculo.getUsuario().getIdUsuario(),
                        vinculo.getUsuario().getNuCpf(),
                        vinculo.getUsuario().getNmUsuario(),
                        vinculo.getDsRole()))
                .toList();
    }

    public void vincularUsuario(Long idOrganizacao, VinculoUsuarioRequestDTO request) {
        buscarEntidade(idOrganizacao);

        var usuarioExistente = usuarioGlobalRepository.findByNuCpf(request.nuCpf());
        CentralUsuarioGlobal usuario;

        if (usuarioExistente.isPresent()) {
            usuario = usuarioExistente.get();
            if (usuario.getTpGlobal() == TipoGlobal.SUPER_ADMIN) {
                throw new BusinessException("Super administradores nao podem ser vinculados a organizacoes");
            }
            if (!usuario.isFlAtivo()) {
                throw new BusinessException("Usuario inativo");
            }
            if (usuarioOrganizacaoRepository.existsByIdOrganizacaoAndIdUsuarioAndFlAtivoTrue(
                    idOrganizacao, usuario.getIdUsuario())) {
                throw new ConflictException("Usuario ja vinculado a esta organizacao");
            }
            usuario = atualizarDadosUsuarioGlobal(usuario, request.nmUsuario(), request.dsSenha());
        } else {
            if (!StringUtils.hasText(request.dsSenha())) {
                throw new BusinessException("Senha obrigatoria para cadastrar novo usuario");
            }
            usuario = criarUsuarioGlobal(request);
        }

        CentralUsuarioOrganizacao vinculo = usuarioOrganizacaoRepository
                .findByIdUsuarioAndIdOrganizacao(usuario.getIdUsuario(), idOrganizacao)
                .orElseGet(CentralUsuarioOrganizacao::new);

        vinculo.setIdUsuario(usuario.getIdUsuario());
        vinculo.setIdOrganizacao(idOrganizacao);
        vinculo.setDsRole(request.dsRole());
        vinculo.setFlAtivo(true);
        usuarioOrganizacaoRepository.save(vinculo);
    }

    public void atualizarVinculo(Long idOrganizacao, Long idUsuario, VinculoUsuarioUpdateDTO request) {
        buscarEntidade(idOrganizacao);
        validarVinculoAtivo(idOrganizacao, idUsuario);

        CentralUsuarioGlobal usuario = usuarioGlobalRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
        atualizarDadosUsuarioGlobal(usuario, request.nmUsuario(), request.dsSenha());

        CentralUsuarioOrganizacao vinculo = usuarioOrganizacaoRepository
                .findByIdUsuarioAndIdOrganizacao(idUsuario, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculo de usuario nao encontrado"));
        vinculo.setDsRole(request.dsRole());
        usuarioOrganizacaoRepository.save(vinculo);
    }

    public void excluirVinculo(Long idOrganizacao, Long idUsuario) {
        buscarEntidade(idOrganizacao);
        validarVinculoAtivo(idOrganizacao, idUsuario);

        CentralUsuarioOrganizacao vinculo = usuarioOrganizacaoRepository
                .findByIdUsuarioAndIdOrganizacao(idUsuario, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculo de usuario nao encontrado"));
        vinculo.setFlAtivo(false);
        usuarioOrganizacaoRepository.save(vinculo);
    }

    private CentralUsuarioGlobal criarUsuarioGlobal(VinculoUsuarioRequestDTO request) {
        CentralUsuarioGlobal usuario = new CentralUsuarioGlobal();
        usuario.setNuCpf(request.nuCpf());
        usuario.setNmUsuario(request.nmUsuario());
        usuario.setDsSenha(passwordEncoder.encode(request.dsSenha()));
        usuario.setTpGlobal(TipoGlobal.DEFAULT);
        usuario.setFlAtivo(true);
        return usuarioGlobalRepository.save(usuario);
    }

    private CentralUsuarioGlobal atualizarDadosUsuarioGlobal(
            CentralUsuarioGlobal usuario, String nmUsuario, String dsSenha) {
        usuario.setNmUsuario(nmUsuario);
        if (StringUtils.hasText(dsSenha)) {
            if (dsSenha.length() < 6) {
                throw new BusinessException("Senha deve ter no minimo 6 caracteres");
            }
            usuario.setDsSenha(passwordEncoder.encode(dsSenha));
        }
        return usuarioGlobalRepository.save(usuario);
    }

    private void validarVinculoAtivo(Long idOrganizacao, Long idUsuario) {
        if (!usuarioOrganizacaoRepository.existsByIdOrganizacaoAndIdUsuarioAndFlAtivoTrue(idOrganizacao, idUsuario)) {
            throw new ResourceNotFoundException("Vinculo de usuario nao encontrado");
        }
    }

    private CentralOrganizacao buscarEntidade(Long idOrganizacao) {
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
    }

    private void agendarProvisionamento(Long idOrganizacao, String slug, String databaseName) {
        if (!autoProvisioningEnabled) {
            return;
        }
        TenantProvisioningService provisioningService = tenantProvisioningService.getIfAvailable();
        if (provisioningService == null) {
            return;
        }
        TenantProvisioningPlan plan = new TenantProvisioningPlan(idOrganizacao, slug, databaseName);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                provisioningService.provisionar(plan);
            }
        });
    }
}
