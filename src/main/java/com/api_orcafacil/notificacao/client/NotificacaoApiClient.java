package com.api_orcafacil.notificacao.client;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.api_orcafacil.notificacao.config.NotificacaoProperties;
import com.api_orcafacil.notificacao.dto.NotificacaoCanal;
import com.api_orcafacil.notificacao.dto.NotificacaoCredenciais;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoEnviarResponse;
import com.api_orcafacil.notificacao.dto.NotificacaoLoginRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoLoginResponse;
import com.api_orcafacil.notificacao.dto.NotificacaoSelecionarOrganizacaoRequest;
import com.api_orcafacil.notificacao.dto.NotificacaoSelecionarOrganizacaoResponse;
import com.api_orcafacil.notificacao.dto.NotificacaoTemplateEnviarRequest;

@Component
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class NotificacaoApiClient {

    private static final String HEADER_API_KEY = "X-API-KEY";

    private final RestClient restClient;
    private final NotificacaoProperties properties;
    private final Map<Long, TokenCache> tokensPorOrganizacao = new ConcurrentHashMap<>();

    public NotificacaoApiClient(NotificacaoProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(criarRequestFactory(properties))
                .build();
    }

    public NotificacaoEnviarResponse enviar(NotificacaoCredenciais credenciais, NotificacaoEnviarRequest request) {
        return post(credenciais, "/app/notificacoes/enviar", request, NotificacaoEnviarResponse.class);
    }

    public NotificacaoEnviarResponse enviarTemplate(
            NotificacaoCredenciais credenciais, NotificacaoTemplateEnviarRequest request) {
        return post(credenciais, "/app/notificacoes/templates/enviar", request, NotificacaoEnviarResponse.class);
    }

    public void registrarConsentimento(
            NotificacaoCredenciais credenciais, NotificacaoCanal canal, String destinatario, String nmContato) {
        Map<String, Object> body = Map.of(
                "canal", canal.name(),
                "destinatario", destinatario,
                "nmContato", nmContato != null ? nmContato : destinatario);
        post(credenciais, "/app/contatos/consentimento", body, Void.class);
    }

    /** Verifica conectividade e autenticacao para as credenciais informadas. */
    public void verificarConexao(NotificacaoCredenciais credenciais) {
        validarCredenciais(credenciais);
        if (credenciais.usaApiKey()) {
            obterStatusIntegracao(credenciais);
            return;
        }
        obterToken(credenciais.idOrganizacaoNotificacao());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> obterStatusIntegracao(NotificacaoCredenciais credenciais) {
        validarCredenciais(credenciais);
        return get(credenciais, "/app/integracao/status", Map.class);
    }

    private void validarCredenciais(NotificacaoCredenciais credenciais) {
        if (credenciais.usaApiKey()) {
            return;
        }
        if (credenciais.idOrganizacaoNotificacao() == null) {
            throw new IllegalStateException("ID da organizacao na notificacao-api nao configurado");
        }
        if (!StringUtils.hasText(properties.getLogin()) || !StringUtils.hasText(properties.getSenha())) {
            throw new IllegalStateException(
                    "Configure NOTIFICACAO_API_KEY ou NOTIFICACAO_LOGIN/NOTIFICACAO_SENHA");
        }
    }

    private String obterToken(Long idOrganizacaoNotificacao) {
        Long orgId = idOrganizacaoNotificacao != null ? idOrganizacaoNotificacao : properties.getIdOrganizacao();
        if (orgId == null) {
            throw new IllegalStateException("ID da organizacao na notificacao-api nao configurado");
        }

        TokenCache cache = tokensPorOrganizacao.get(orgId);
        if (cache != null && Instant.now().isBefore(cache.expiraEm())) {
            return cache.token();
        }

        synchronized (tokensPorOrganizacao) {
            cache = tokensPorOrganizacao.get(orgId);
            if (cache != null && Instant.now().isBefore(cache.expiraEm())) {
                return cache.token();
            }
            String token = autenticar(orgId);
            tokensPorOrganizacao.put(orgId, new TokenCache(token, Instant.now().plusSeconds(50 * 60)));
            return token;
        }
    }

    private String autenticar(Long idOrganizacaoNotificacao) {
        NotificacaoLoginResponse login = restClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new NotificacaoLoginRequest(properties.getLogin(), properties.getSenha()))
                .retrieve()
                .body(NotificacaoLoginResponse.class);

        if (login == null || login.token() == null) {
            throw new IllegalStateException("Falha ao autenticar na notificacao-api");
        }

        NotificacaoSelecionarOrganizacaoResponse selecao = restClient.post()
                .uri("/auth/selecionar-organizacao")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + login.token())
                .body(new NotificacaoSelecionarOrganizacaoRequest(idOrganizacaoNotificacao))
                .retrieve()
                .body(NotificacaoSelecionarOrganizacaoResponse.class);

        if (selecao == null || selecao.token() == null) {
            throw new IllegalStateException("Falha ao selecionar organizacao " + idOrganizacaoNotificacao
                    + " na notificacao-api");
        }
        return selecao.token();
    }

    private <T> T post(NotificacaoCredenciais credenciais, String path, Object body, Class<T> responseType) {
        try {
            return executarPost(credenciais, path, body, responseType);
        } catch (RestClientResponseException ex) {
            if (!credenciais.usaApiKey() && ex.getStatusCode().value() == 401) {
                Long orgId = credenciais.idOrganizacaoNotificacao() != null
                        ? credenciais.idOrganizacaoNotificacao()
                        : properties.getIdOrganizacao();
                tokensPorOrganizacao.remove(orgId);
                return executarPost(credenciais, path, body, responseType);
            }
            throw ex;
        }
    }

    private <T> T get(NotificacaoCredenciais credenciais, String path, Class<T> responseType) {
        RestClient.RequestHeadersSpec<?> spec = restClient.get().uri(path);
        aplicarAutenticacao(spec, credenciais);
        return spec.retrieve().body(responseType);
    }

    private <T> T executarPost(NotificacaoCredenciais credenciais, String path, Object body, Class<T> responseType) {
        RestClient.RequestBodySpec spec = restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON);
        aplicarAutenticacao(spec, credenciais);
        if (responseType == Void.class) {
            spec.body(body).retrieve().toBodilessEntity();
            return null;
        }
        return spec.body(body).retrieve().body(responseType);
    }

    private void aplicarAutenticacao(RestClient.RequestHeadersSpec<?> spec, NotificacaoCredenciais credenciais) {
        if (credenciais.usaApiKey()) {
            spec.header(HEADER_API_KEY, credenciais.apiKey());
            return;
        }
        spec.header("Authorization", "Bearer " + obterToken(credenciais.idOrganizacaoNotificacao()));
    }

    private static SimpleClientHttpRequestFactory criarRequestFactory(NotificacaoProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(properties.getReadTimeoutMs()));
        return factory;
    }

    private record TokenCache(String token, Instant expiraEm) {
    }
}
