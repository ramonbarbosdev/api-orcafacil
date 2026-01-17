package com.api_orcafacil.domain.orcamento.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoStatusHistorico;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoStatusHistoricoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.enums.StatusOrcamento;

import jakarta.transaction.Transactional;

@Service
public class OrcamentoStatusHistoricoService {

    @Autowired
    private OrcamentoStatusHistoricoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    @Transactional()
    public void registrar(
            Orcamento orcamento,
            StatusOrcamento statusAnterior,
            StatusOrcamento statusNovo,
            String idTenant
        ) throws Exception {

        Usuario usuario = validacaoService.obterUsuarioLogado();

        OrcamentoStatusHistorico historico = new OrcamentoStatusHistorico();

        historico.setOrcamento(orcamento);
        historico.setIdTenant(idTenant);
        historico.setTpStatusAnterior(statusAnterior);
        historico.setTpStatusAtual(statusNovo);
        historico.setUsuario(usuario);

        repository.save(historico);
    }

    @Transactional()
    public List<OrcamentoStatusHistorico> listarPorOrcamento(
            Long idOrcamento) {

        return repository.findByOrcamento_IdOrcamentoOrderByDtCadastroAsc(idOrcamento);
    }

    public void excluirPorIdOrcamento(Long id)
    {
        repository.deleteByIdMestre(id);
    }
}
