package com.api_orcafacil.service.logo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.dto.organizacao.OrganizacaoLogoMetadadosDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.repository.central.CentralOrganizacaoLogoRepository;
import com.api_orcafacil.service.OrcamentoPublicoService;
import com.api_orcafacil.service.PoliticaPlanoService;
import com.api_orcafacil.service.TenantContextService;
import com.api_orcafacil.service.logo.LogoImagemValidacaoService.ResultadoValidacao;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoLogo;

@Service
public class OrganizacaoLogoService {

    private static final Logger log = LoggerFactory.getLogger(OrganizacaoLogoService.class);

    public static final String URL_LOGO_AUTENTICADA = "/organizacao/logo/imagem";
    public static final String URL_LOGO_PUBLICA_PREFIXO = "/orcamentos/visualizacao/";

    private final CentralOrganizacaoLogoRepository logoRepository;
    private final LogoArmazenamentoLocalService armazenamento;
    private final LogoImagemValidacaoService validacao;
    private final TenantContextService tenantContextService;
    private final OrcamentoRepository orcamentoRepository;
    private final ObjectProvider<PoliticaPlanoService> politicaPlanoService;
    private final ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService;

    public OrganizacaoLogoService(
            CentralOrganizacaoLogoRepository logoRepository,
            LogoArmazenamentoLocalService armazenamento,
            LogoImagemValidacaoService validacao,
            TenantContextService tenantContextService,
            OrcamentoRepository orcamentoRepository,
            ObjectProvider<PoliticaPlanoService> politicaPlanoService,
            ObjectProvider<OrcamentoPublicoService> orcamentoPublicoService) {
        this.logoRepository = logoRepository;
        this.armazenamento = armazenamento;
        this.validacao = validacao;
        this.tenantContextService = tenantContextService;
        this.orcamentoRepository = orcamentoRepository;
        this.politicaPlanoService = politicaPlanoService;
        this.orcamentoPublicoService = orcamentoPublicoService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoLogoMetadadosDTO obterMetadadosAtual() {
        return obterMetadados(tenantContextService.idOrganizacaoObrigatoria(), URL_LOGO_AUTENTICADA);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoLogoMetadadosDTO obterMetadados(Long idOrganizacao, String urlBase) {
        return logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao)
                .map(logo -> paraMetadados(logo, urlBase))
                .orElseGet(() -> new OrganizacaoLogoMetadadosDTO(false, null, null, null, null, null, null));
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public ConteudoLogo obterConteudoAtual() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        CentralOrganizacaoLogo logo = buscarLogoAtiva(idOrganizacao);
        return carregarConteudo(logo);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public ConteudoLogo obterConteudoPorOrganizacao(Long idOrganizacao) {
        CentralOrganizacaoLogo logo = buscarLogoAtiva(idOrganizacao);
        return carregarConteudo(logo);
    }

    @Transactional(readOnly = true)
    public ConteudoLogo obterConteudoPublico(String cdPublico) {
        OrcamentoPublicoService publicoService = orcamentoPublicoService.getIfAvailable();
        if (publicoService != null) {
            return publicoService.executarComCdPublico(cdPublico, ref -> obterConteudoPublicoInterno(ref.idOrganizacao()));
        }
        Orcamento orcamento = orcamentoRepository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        return obterConteudoPublicoInterno(orcamento.getIdOrganizacao());
    }

    private ConteudoLogo obterConteudoPublicoInterno(Long idOrganizacao) {
        Optional<CentralOrganizacaoLogo> logo = logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao);
        if (logo.isEmpty()) {
            throw new ResourceNotFoundException("Logo nao encontrada");
        }
        return carregarConteudo(logo.get());
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public OrganizacaoLogoMetadadosDTO enviarOuSubstituir(MultipartFile file) throws IOException {
        validarPlanoParaAlteracao();
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Long idUsuario = tenantContextService.idUsuario();
        ResultadoValidacao resultado = validacao.validar(file);

        logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao).ifPresent(logoAntiga -> {
            try {
                armazenamento.remover(logoAntiga.getDsCaminhoInterno(), idOrganizacao);
            } catch (IOException ex) {
                log.warn("Falha ao remover arquivo antigo da logo da organizacao {}: {}", idOrganizacao, ex.getMessage());
            }
            logoRepository.delete(logoAntiga);
        });

        String caminhoInterno = armazenamento.salvar(idOrganizacao, resultado.bytes(), resultado.extensao());
        String nomeSalvo = extrairNomeSalvo(caminhoInterno);

        CentralOrganizacaoLogo logo = new CentralOrganizacaoLogo();
        logo.setIdOrganizacao(idOrganizacao);
        logo.setDsCaminhoInterno(caminhoInterno);
        logo.setNmArquivoOriginal(sanitizarNomeOriginal(file.getOriginalFilename()));
        logo.setNmArquivoSalvo(nomeSalvo);
        logo.setDsContentType(resultado.contentType());
        logo.setDsExtensao(resultado.extensao());
        logo.setNuTamanhoBytes((long) resultado.bytes().length);
        logo.setNuLargura(resultado.largura());
        logo.setNuAltura(resultado.altura());
        logo.setIdUsuarioUpload(idUsuario);
        logo.setFlAtivo(true);

        CentralOrganizacaoLogo salva = logoRepository.save(logo);
        log.info("Logo da organizacao {} enviada por usuario {}", idOrganizacao, idUsuario);
        return paraMetadados(salva, URL_LOGO_AUTENTICADA);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void remover() throws IOException {
        validarPlanoParaRemocao();
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        CentralOrganizacaoLogo logo = logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Logo nao encontrada"));
        armazenamento.remover(logo.getDsCaminhoInterno(), idOrganizacao);
        logoRepository.delete(logo);
        log.info("Logo da organizacao {} removida por usuario {}", idOrganizacao, tenantContextService.idUsuario());
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public String resolverCaminhoFisicoParaRelatorio(Long idOrganizacao) {
        return logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao)
                .map(logo -> {
                    try {
                        Path path = armazenamento.caminhoAbsoluto(logo.getDsCaminhoInterno(), idOrganizacao);
                        if (path.toFile().exists()) {
                            return path.toString();
                        }
                    } catch (Exception ex) {
                        log.warn("Logo indisponivel para relatorio da organizacao {}: {}", idOrganizacao, ex.getMessage());
                    }
                    return null;
                })
                .orElse(null);
    }

    public String urlLogoPublica(String cdPublico) {
        if (!logoPublicaDisponivel(cdPublico)) {
            return null;
        }
        return URL_LOGO_PUBLICA_PREFIXO + cdPublico + "/logo";
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public boolean possuiLogo(Long idOrganizacao) {
        return logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao).isPresent();
    }

    private boolean logoPublicaDisponivel(String cdPublico) {
        OrcamentoPublicoService publicoService = orcamentoPublicoService.getIfAvailable();
        if (publicoService != null) {
            try {
                return publicoService.executarComCdPublico(cdPublico, ref ->
                        logoRepository.findByIdOrganizacaoAndFlAtivoTrue(ref.idOrganizacao()).isPresent());
            } catch (ResourceNotFoundException ex) {
                return false;
            }
        }
        return orcamentoRepository.findByCdPublico(cdPublico)
                .map(Orcamento::getIdOrganizacao)
                .flatMap(logoRepository::findByIdOrganizacaoAndFlAtivoTrue)
                .isPresent();
    }

    private void validarPlanoParaAlteracao() {
        PoliticaPlanoService politica = politicaPlanoService.getIfAvailable();
        if (politica != null) {
            politica.validarRecursoAtual("organizacao.criar");
        }
    }

    private void validarPlanoParaRemocao() {
        PoliticaPlanoService politica = politicaPlanoService.getIfAvailable();
        if (politica != null) {
            politica.validarRecursoAtual("organizacao.deletar");
        }
    }

    private CentralOrganizacaoLogo buscarLogoAtiva(Long idOrganizacao) {
        return logoRepository.findByIdOrganizacaoAndFlAtivoTrue(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Logo nao encontrada"));
    }

    private ConteudoLogo carregarConteudo(CentralOrganizacaoLogo logo) {
        try {
            byte[] bytes = armazenamento.ler(logo.getDsCaminhoInterno(), logo.getIdOrganizacao());
            return new ConteudoLogo(bytes, logo.getDsContentType(), logo.getDtAtualizacao());
        } catch (IOException ex) {
            throw new BusinessException("Erro ao carregar a logo");
        }
    }

    private OrganizacaoLogoMetadadosDTO paraMetadados(CentralOrganizacaoLogo logo, String url) {
        return new OrganizacaoLogoMetadadosDTO(
                true,
                url,
                logo.getDsContentType(),
                logo.getNuTamanhoBytes(),
                logo.getNuLargura(),
                logo.getNuAltura(),
                logo.getDtAtualizacao());
    }

    private String extrairNomeSalvo(String caminhoInterno) {
        int barra = caminhoInterno.lastIndexOf('/');
        return barra >= 0 ? caminhoInterno.substring(barra + 1) : caminhoInterno;
    }

    private String sanitizarNomeOriginal(String nomeOriginal) {
        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            return "logo";
        }
        String nome = nomeOriginal.replace("\\", "/");
        int barra = nome.lastIndexOf('/');
        if (barra >= 0) {
            nome = nome.substring(barra + 1);
        }
        return nome.length() > 255 ? nome.substring(0, 255) : nome;
    }

    public record ConteudoLogo(byte[] bytes, String contentType, java.time.LocalDateTime atualizadaEm) {
    }
}
