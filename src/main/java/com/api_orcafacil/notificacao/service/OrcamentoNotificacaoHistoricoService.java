package com.api_orcafacil.notificacao.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.orcamento.OrcamentoEnviarResponse.ResultadoNotificacao;
import com.api_orcafacil.model.OrcamentoNotificacaoEnviada;
import com.api_orcafacil.notificacao.dto.NotificacaoCanal;
import com.api_orcafacil.repository.OrcamentoNotificacaoEnviadaRepository;
import com.api_orcafacil.service.TenantContextService;

@Service
@ConditionalOnProperty(name = "app.notificacao.enabled", havingValue = "true")
public class OrcamentoNotificacaoHistoricoService {

    private final OrcamentoNotificacaoEnviadaRepository repository;
    private final TenantContextService tenantContextService;

    public OrcamentoNotificacaoHistoricoService(
            OrcamentoNotificacaoEnviadaRepository repository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void registrar(
            Long idOrcamento,
            ResultadoNotificacao resultado,
            String mensagem) {
        OrcamentoNotificacaoEnviada registro = new OrcamentoNotificacaoEnviada();
        registro.setIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
        registro.setIdOrcamento(idOrcamento);
        if (resultado.getCanal() != null) {
            registro.setTpCanal(resultado.getCanal().name());
        }
        registro.setDsDestinatario(resultado.getDestinatario());
        registro.setFlSucesso(resultado.isSucesso());
        registro.setIdNotificacaoExterna(resultado.getIdNotificacao());
        registro.setDsErro(resultado.getErro());
        registro.setDsMensagem(mensagem);
        repository.save(registro);
    }
}
