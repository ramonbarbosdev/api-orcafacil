package com.api_orcafacil.notificacao.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.dto.orcamento.OrcamentoEnviarRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoEnviarResponse.ResultadoNotificacao;
import com.api_orcafacil.dto.orcamento.OrcamentoMensagemCompartilhamentoResponse;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.notificacao.client.NotificacaoApiClient;
import com.api_orcafacil.notificacao.config.NotificacaoProperties;
import com.api_orcafacil.notificacao.dto.NotificacaoAlertaRegistrarRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoCanal;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarResponse;
import com.api_orcafacil.notificacao.support.NotificacaoErroParser;
import com.api_orcafacil.notificacao.support.NotificacaoErroUsuario;
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

    public List<ResultadoNotificacao> notificarOrcamentoEnviado(Orcamento orcamento, OrcamentoEnviarRequest request) {
        List<NotificacaoCanal> canaisSolicitados = request != null ? request.getCanais() : List.of();
        String mensagemPersonalizada = request != null ? request.getMensagem() : null;

        Cliente cliente = clienteRepository.findByIdClienteAndIdOrganizacao(
                        orcamento.getIdCliente(), orcamento.getIdOrganizacao())
                .orElse(null);
        if (cliente == null) {
            return List.of(erro(null, null, "Cliente do orcamento nao encontrado"));
        }

        Set<NotificacaoCanal> canaisEfetivos = resolverCanais(canaisSolicitados, cliente);
        if (canaisEfetivos.isEmpty()) {
            return List.of(erro(null, null, "Nenhum canal disponivel para o cliente"));
        }

        NotificacaoCredenciais credenciais = organizacaoResolver.resolverCredenciais(orcamento.getIdOrganizacao());
        if (!organizacaoResolver.integracaoConfigurada(orcamento.getIdOrganizacao())) {
            return List.of();
        }

        String link = montarLinkPublico(orcamento.getCdPublico());
        String mensagem = resolverMensagem(orcamento, cliente, link, mensagemPersonalizada);
        List<ResultadoNotificacao> resultados = new ArrayList<>();

        for (NotificacaoCanal canal : canaisEfetivos) {
            resultados.add(enviarPorCanal(credenciais, canal, orcamento, cliente, link, mensagem, request));
        }
        return resultados;
    }

    public OrcamentoMensagemCompartilhamentoResponse previewMensagemCompartilhamento(Orcamento orcamento) {
        Cliente cliente = clienteRepository.findByIdClienteAndIdOrganizacao(
                        orcamento.getIdCliente(), orcamento.getIdOrganizacao())
                .orElse(null);
        if (cliente == null) {
            throw new IllegalStateException("Cliente do orcamento nao encontrado");
        }
        String link = montarLinkPublico(orcamento.getCdPublico());
        String mensagem = montarMensagemPadrao(orcamento, cliente, link);
        return new OrcamentoMensagemCompartilhamentoResponse(mensagem, link, false);
    }

    public String resolverMensagemPreview(Orcamento orcamento, String mensagemPersonalizada) {
        Cliente cliente = clienteRepository.findByIdClienteAndIdOrganizacao(
                        orcamento.getIdCliente(), orcamento.getIdOrganizacao())
                .orElse(null);
        if (cliente == null) {
            return mensagemPersonalizada;
        }
        String link = montarLinkPublico(orcamento.getCdPublico());
        return resolverMensagem(orcamento, cliente, link, mensagemPersonalizada);
    }

    private ResultadoNotificacao enviarPorCanal(
            NotificacaoCredenciais credenciais,
            NotificacaoCanal canal,
            Orcamento orcamento,
            Cliente cliente,
            String link,
            String mensagem,
            OrcamentoEnviarRequest request) {
        String destinatario = resolverDestinatario(canal, cliente, request);
        if (!StringUtils.hasText(destinatario)) {
            return erro(canal, destinatario, "Destinatario nao informado para o canal " + canal);
        }

        try {
            if (canal == NotificacaoCanal.WHATSAPP && properties.isRegistrarConsentimentoWhatsapp()) {
                client.registrarConsentimento(credenciais, canal, destinatario, cliente.getNmCliente());
            }

            String assunto = "Orcamento " + orcamento.getNuOrcamento();
            NotificacaoEnviarResponse resposta = client.enviar(
                    credenciais, new NotificacaoEnviarRequest(canal, destinatario, assunto, mensagem));
            boolean sucesso = Boolean.TRUE.equals(resposta.sucesso());
            if (!sucesso) {
                NotificacaoErroUsuario erroUsuario = NotificacaoErroParser.interpretarErroEnvio(resposta.erro());
                return erroComAlerta(
                        credenciais,
                        canal,
                        destinatario,
                        orcamento,
                        erroUsuario,
                        resposta.idNotificacao(),
                        resposta.status());
            }
            return new ResultadoNotificacao(
                    canal,
                    destinatario,
                    true,
                    resposta.idNotificacao(),
                    resposta.status(),
                    null,
                    null,
                    null,
                    false,
                    resposta.tempoEstimadoEnvioSegundos(),
                    resposta.posicaoFila(),
                    resposta.tempoEstimadoEnvioTexto());
        } catch (RestClientResponseException ex) {
            log.warn("Falha ao enviar notificacao {} para {}: {}", canal, destinatario, ex.getMessage());
            NotificacaoErroUsuario erroUsuario = NotificacaoErroParser.interpretar(ex);
            return erroComAlerta(credenciais, canal, destinatario, orcamento, erroUsuario, null, null);
        } catch (Exception ex) {
            log.warn("Erro inesperado ao enviar notificacao {} para {}", canal, destinatario, ex);
            NotificacaoErroUsuario erroUsuario = NotificacaoErroParser.interpretarGenerico(ex);
            return erroComAlerta(credenciais, canal, destinatario, orcamento, erroUsuario, null, null);
        }
    }

    private ResultadoNotificacao erroComAlerta(
            NotificacaoCredenciais credenciais,
            NotificacaoCanal canal,
            String destinatario,
            Orcamento orcamento,
            NotificacaoErroUsuario erroUsuario,
            Long idNotificacao,
            String status) {
        boolean equipeNotificada = false;
        if (erroUsuario.notificarEquipe()
                && NotificacaoErroParser.deveNotificarEquipe(erroUsuario.codigoErro())) {
            equipeNotificada = registrarAlertaEquipe(
                    credenciais,
                    canal,
                    destinatario,
                    orcamento,
                    erroUsuario,
                    idNotificacao);
        }
        return erro(canal, destinatario, erroUsuario, equipeNotificada, idNotificacao, status);
    }

    private boolean registrarAlertaEquipe(
            NotificacaoCredenciais credenciais,
            NotificacaoCanal canal,
            String destinatario,
            Orcamento orcamento,
            NotificacaoErroUsuario erroUsuario,
            Long idNotificacao) {
        try {
            String titulo = "Falha ao enviar orcamento " + orcamento.getNuOrcamento();
            String mensagem = """
                    Falha na integracao OrcaFacil ao enviar notificacao.

                    Orcamento: %s (ID %s)
                    Canal: %s
                    Destinatario: %s
                    Codigo: %s
                    Detalhe tecnico: %s
                    """.formatted(
                    orcamento.getNuOrcamento(),
                    orcamento.getIdOrcamento(),
                    canal,
                    destinatario,
                    erroUsuario.codigoErro(),
                    erroUsuario.mensagemTecnica()).trim();
            client.registrarAlertaOperacional(
                    credenciais,
                    new NotificacaoAlertaRegistrarRequest(
                            titulo,
                            mensagem,
                            destinatario,
                            canal != null ? canal.name() : null,
                            erroUsuario.codigoErro(),
                            idNotificacao));
            return true;
        } catch (Exception ex) {
            log.warn("Nao foi possivel registrar alerta operacional: {}", ex.getMessage());
            return false;
        }
    }

    private String resolverMensagem(
            Orcamento orcamento, Cliente cliente, String link, String mensagemPersonalizada) {
        if (StringUtils.hasText(mensagemPersonalizada)) {
            return aplicarPlaceholders(mensagemPersonalizada.trim(), orcamento, cliente, link);
        }
        return montarMensagemPadrao(orcamento, cliente, link);
    }

    private String aplicarPlaceholders(
            String texto, Orcamento orcamento, Cliente cliente, String link) {
        return texto
                .replace("{nomeCliente}", valor(cliente.getNmCliente()))
                .replace("{numeroOrcamento}", valor(orcamento.getNuOrcamento()))
                .replace("{valorTotal}", formatarMoeda(orcamento.getVlPrecoFinal()))
                .replace("{dataValidade}", orcamento.getDtValido() != null
                        ? orcamento.getDtValido().format(DATA_BR)
                        : "")
                .replace("{linkOrcamento}", valor(link));
    }

    private String valor(String texto) {
        return texto != null ? texto : "";
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

    private String resolverDestinatario(NotificacaoCanal canal, Cliente cliente, OrcamentoEnviarRequest request) {
        return switch (canal) {
            case WHATSAPP -> {
                String informado = request != null ? request.getNuTelefone() : null;
                String telefone = StringUtils.hasText(informado) ? informado : cliente.getNuTelefone();
                yield normalizarTelefone(telefone);
            }
            case EMAIL -> {
                String informado = request != null ? request.getDsEmail() : null;
                String email = StringUtils.hasText(informado) ? informado : cliente.getDsEmail();
                yield email != null ? email.trim() : null;
            }
            default -> null;
        };
    }

    private String montarLinkPublico(String cdPublico) {
        String base = properties.getPublicBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String path = properties.getPublicOrcamentoPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return base + path + "/" + cdPublico;
    }

    private String montarMensagemPadrao(Orcamento orcamento, Cliente cliente, String link) {
        return """
                Ola, %s! 👋

                Seu orcamento *%s* no valor de *%s* esta disponivel.
                Validade: %s

                Visualize aqui:
                %s
                """.formatted(
                cliente.getNmCliente(),
                orcamento.getNuOrcamento(),
                formatarMoeda(orcamento.getVlPrecoFinal()),
                orcamento.getDtValido() != null ? orcamento.getDtValido().format(DATA_BR) : "nao informada",
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
        return null;
    }

    private ResultadoNotificacao erro(NotificacaoCanal canal, String destinatario, String mensagem) {
        return erro(
                canal,
                destinatario,
                new NotificacaoErroUsuario("ERRO_ENVIO", mensagem, mensagem, false),
                false,
                null,
                null);
    }

    private ResultadoNotificacao erro(
            NotificacaoCanal canal,
            String destinatario,
            NotificacaoErroUsuario erroUsuario,
            boolean equipeNotificada,
            Long idNotificacao,
            String status) {
        return new ResultadoNotificacao(
                canal,
                destinatario,
                false,
                idNotificacao,
                status,
                erroUsuario.mensagemTecnica(),
                erroUsuario.mensagemUsuario(),
                erroUsuario.codigoErro(),
                equipeNotificada,
                null,
                null,
                null);
    }
}
