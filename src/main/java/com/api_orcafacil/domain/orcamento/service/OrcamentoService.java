package com.api_orcafacil.domain.orcamento.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.cliente.service.ClienteService;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.MestreDetalheUtils;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository repository;

    @Autowired
    private OrcamentoItemRepository orcamentoItemRepository;

    @Autowired
    private ValidacaoService validacaoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ConfiguracaoOrcamentoService configuracaoOrcamentoService;

    @Autowired
    private OrcamentoItemCampoValorService orcamentoItemCampoValorService ;

    public static final Function<Orcamento, Long> ID_FUNCTION = Orcamento::getIdOrcamento;

    public static final Function<Orcamento, String> SEQUENCIA_FUNCTION = Orcamento::getNuOrcamento;

    @Transactional(rollbackFor = Exception.class)
    public Orcamento salvar(Orcamento objeto) throws Exception {

        List<OrcamentoItem> itensOrcamentoItem = objeto.getOrcamentoItem();
        objeto.setOrcamentoItem(null);

        validarObjeto(objeto);
        Cliente clientePersistido = clienteService.registrarClienteAPartirDoOrcamento(objeto);

        objeto.setCliente(clientePersistido); // redundante, mas seguro

        objeto = repository.save(objeto);
        salvarOrcamentoItemDetalhe(objeto, itensOrcamentoItem);

        return repository.save(objeto);
    }

    public void salvarOrcamentoItemDetalhe(Orcamento objeto,
            List<OrcamentoItem> itens) throws Exception {

        Function<Orcamento, Long> getIdFunctionMestre = Orcamento::getIdOrcamento;
        Function<OrcamentoItem, Long> getIdFunction = OrcamentoItem::getIdOrcamentoItem;

        Long idMestre = getIdFunctionMestre.apply(objeto);

        MestreDetalheUtils.removerItensGenerico(
                idMestre,
                itens,
                orcamentoItemRepository::findbyIdMestre,
                orcamentoItemRepository::deleteById,
                getIdFunction);

        if (itens != null && itens.size() > 0) {
            for (OrcamentoItem item : itens) {
                item.setIdOrcamento(idMestre);

                Long idExistente = getIdFunction.apply(item);

                if (idExistente == null || idExistente == 0) {
                    item.setIdOrcamentoItem(null);
                }

                OrcamentoItem itemSalvo = orcamentoItemRepository.save(item);

                List<OrcamentoItemCampoValor> campos = item.getOrcamentoItemCampoValor();

                orcamentoItemCampoValorService.salvar(campos, itemSalvo);

            }

            objeto.setOrcamentoItem(itens);
        }
    }

    public void validarObjeto(Orcamento objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
    }

    public String sequencia(String idTenant) throws Exception {

        ConfiguracaoOrcamento config = configuracaoOrcamentoService.obterPrimeiroObjeto(idTenant);

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        String sequenciaFinal = config.getPrefixoNumero() + "-" + sq_sequencia;

        return sequenciaFinal;
    }
}
