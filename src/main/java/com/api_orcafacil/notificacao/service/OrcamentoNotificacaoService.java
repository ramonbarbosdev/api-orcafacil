package com.api_orcafacil.notificacao.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.dto.orcamento.OrcamentoEnviarResponse.ResultadoNotificacao;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.config.NotificacaoProperties;
import com.api_orcafacil.notificacao.dto.NotificacaoCanal;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarResponse;
import com.api_orcafacil.notificacao.dto.NotificacaoTemplateEnviarRequest;
import com.api_orcafacil.notificacao.support.NotificacaoErroParser;
import com.api_orcafacil.repository.ClienteRepository;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class OrcamentoNotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoNotificacaoService.class);
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NotificacaoApiClient client;
    private final NotificacaoProperties properties;
    private final ClienteRepository clienteRepository;
    private final NotificacaoOrganizacaoResolver organizacaoResolver;

    public OrcamentoNotificacaoService(
            NotificacaoApiClient client,
            NotificacaoProperties properties,
            ClienteRepository clienteRepository,
            NotificacaoOrganizacaoResolver organizacaoResolver) {
        this.client = client;
        this.properties = properties;
        this.clienteRepository = clienteRepository;
        this.organizacaoResolver = organizacaoResolver;
    }

    public List<ResultadoNotificacao> notificarOrcamentoEnviado(Orcamento orcamento, List<NotificacaoCanal> canais) {
        Cliente cliente = clienteRepository.findByIdClienteAndIdOrganizacao(
                        orcamento.getIdCliente(), orcamento.getIdOrganizacao())
                .orElse(null);
        if (cliente == null) {
            return List.of(erro(null, null, "Cliente do orcamento nao encontrado"));
        }

        Set<NotificacaoCanal> canaisEfetivos = resolverCanais(canais, cliente);
        if (canaisEfetivos.isEmpty()) {
            return List.of(erro(null, null, "Nenhum canal disponivel para o cliente"));
        }

        Long idOrgNotificacao = organizacaoResolver.resolverIdOrganizacaoNotificacao(orcamento.getIdOrganizacao());
        NotificacaoCredenciais credenciais = organizacaoResolver.resolverCredenciais(orcamento.getIdOrganizacao());
        if (idOrgNotificacao == null && !credenciais.usaApiKey()) {
            return List.of(erro(null, null,
                    "Organizacao sem mapeamento na notificacao-api. Configure id_organizacao_notificacao, NOTIFICACAO_API_KEY ou NOTIFICACAO_ID_ORGANIZACAO."));
        }

        String link = montarLinkPublico(orcamento.getCdPublico());
        List<ResultadoNotificacao> resultados = new ArrayList<>();

        for (NotificacaoCanal canal : canaisEfetivos) {
            resultados.add(enviarPorCanal(credenciais, canal, orcamento, cliente, link));
        }
        return resultados;
    }

    private ResultadoNotificacao enviarPorCanal(
            NotificacaoCredenciais credenciais, NotificacaoCanal canal, Orcamento orcamento, Cliente cliente, String link) {
        String destinatario = resolverDestinatario(canal, cliente);
        if (!StringUtils.hasText(destinatario)) {
            return erro(canal, destinatario, "Destinatario nao informado para o canal " + canal);
        }

        try {
            if (canal == NotificacaoCanal.WHATSAPP && properties.isRegistrarConsentimentoWhatsapp()) {
                client.registrarConsentimento(credenciais, canal, destinatario, cliente.getNmCliente());
            }

            NotificacaoEnviarResponse resposta = enviarMensagem(
                    credenciais, canal, orcamento, cliente, link, destinatario);
            boolean sucesso = Boolean.TRUE.equals(resposta.sucesso());
            return new ResultadoNotificacao(
                    canal, destinatario, sucesso, resposta.idNotificacao(), resposta.erro());
        } catch (RestClientResponseException ex) {
            log.warn("Falha ao enviar notificacao {} para {}: {}", canal, destinatario, ex.getMessage());
            return erro(canal, destinatario, NotificacaoErroParser.extrairMensagem(ex));
        } catch (Exception ex) {
            log.warn("Erro inesperado ao enviar notificacao {} para {}", canal, destinatario, ex);
            return erro(canal, destinatario, ex.getMessage());
        }
    }

    private NotificacaoEnviarResponse enviarMensagem(
            NotificacaoCredenciais credenciais,
            NotificacaoCanal canal,
            Orcamento orcamento,
            Cliente cliente,
            String link,
            String destinatario) {
        String templateKey = properties.getTemplateOrcamentoEnviado();
        if (StringUtils.hasText(templateKey)) {
            return client.enviarTemplate(credenciais, new NotificacaoTemplateEnviarRequest(
                    templateKey,
                    destinatario,
                    Map.of(
                            "nomeCliente", cliente.getNmCliente(),
                            "numeroOrcamento", orcamento.getNuOrcamento(),
                            "valorTotal", formatarMoeda(orcamento.getVlPrecoFinal()),
                            "dataValidade", orcamento.getDtValido().format(DATA_BR),
                            "linkOrcamento", link)));
        }

        String assunto = "Orcamento " + orcamento.getNuOrcamento();
        String mensagem = montarMensagemTexto(orcamento, cliente, link);
        return client.enviar(credenciais, new NotificacaoEnviarRequest(canal, destinatario, assunto, mensagem));
    }

    private Set<NotificacaoCanal> resolverCanais(List<NotificacaoCanal> solicitados, Cliente cliente) {
        Set<NotificacaoCanal> canais = new LinkedHashSet<>();
        if (solicitados != null && !solicitados.isEmpty()) {
            canais.addAll(solicitados);
            return canais;
        }
        if (StringUtils.hasText(cliente.getNuTelefone())) {
            canais.add(NotificacaoCanal.WHATSAPP);
        }
        if (StringUtils.hasText(cliente.getDsEmail())) {
            canais.add(NotificacaoCanal.EMAIL);
        }
        return canais;
    }

    private String resolverDestinatario(NotificacaoCanal canal, Cliente cliente) {
        return switch (canal) {
            case WHATSAPP -> normalizarTelefone(cliente.getNuTelefone());
            case EMAIL -> cliente.getDsEmail() != null ? cliente.getDsEmail().trim() : null;
            default -> null;
        };
    }

    private String montarLinkPublico(String cdPublico) {
        String base = properties.getPublicBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/orcamentos/visualizacao/" + cdPublico;
    }

    private String montarMensagemTexto(Orcamento orcamento, Cliente cliente, String link) {
        return """
                Ola, %s!

                Seu orcamento %s no valor de %s esta disponivel.
                Validade: %s

                Visualize aqui: %s
                """.formatted(
                cliente.getNmCliente(),
                orcamento.getNuOrcamento(),
                formatarMoeda(orcamento.getVlPrecoFinal()),
                orcamento.getDtValido().format(DATA_BR),
                link).trim();
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null) {
            return null;
        }
        String digits = telefone.replaceAll("\\D", "");
        if (digits.startsWith("55") && digits.length() >= 12) {
            return digits;
        }
        if (digits.length() >= 10) {
            return "55" + digits;
        }
        return digits;
    }

    private ResultadoNotificacao erro(NotificacaoCanal canal, String destinatario, String mensagem) {
        return new ResultadoNotificacao(canal, destinatario, false, null, mensagem);
    }
}
