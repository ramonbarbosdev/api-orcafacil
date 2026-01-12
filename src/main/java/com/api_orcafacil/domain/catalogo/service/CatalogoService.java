package com.api_orcafacil.domain.catalogo.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.catalogo.model.Catalogo;
import com.api_orcafacil.domain.catalogo.model.CatalogoCampo;
import com.api_orcafacil.domain.catalogo.repository.CatalogoCampoRepository;
import com.api_orcafacil.domain.catalogo.repository.CatalogoRepository;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.MestreDetalheUtils;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    @Autowired
    private CatalogoCampoRepository campoRepository;

    public static final Function<Catalogo, Long> ID_FUNCTION = Catalogo::getIdCatalogo;

    public static final Function<Catalogo, String> SEQUENCIA_FUNCTION = Catalogo::getCdCatalogo;

    @Transactional(rollbackFor = Exception.class)
    public Catalogo salvar(Catalogo objeto) throws Exception {

        List<CatalogoCampo> itensCatalogoCampo = objeto.getCatalogoCampo();
        objeto.setCatalogoCampo(null);

        validarObjeto(objeto);

        objeto = repository.save(objeto);
        salvarCatalogoCampoDetalhe(objeto, itensCatalogoCampo);

        return repository.save(objeto);
    }

    public void salvarCatalogoCampoDetalhe(Catalogo objeto,
            List<CatalogoCampo> itens) throws Exception {

        Function<Catalogo, Long> getIdFunctionMestre = Catalogo::getIdCatalogo;
        Function<CatalogoCampo, Long> getIdFunction = CatalogoCampo::getIdCatalogoCampo;

        Long idMestre = getIdFunctionMestre.apply(objeto);

        MestreDetalheUtils.removerItensGenerico(
                idMestre,
                itens,
                campoRepository::findbyIdMestre,
                campoRepository::deleteById,
                getIdFunction);

        if (itens != null && itens.size() > 0) {
            for (CatalogoCampo item : itens) {
                item.setIdCatalogo(idMestre);

                Long idExistente = getIdFunction.apply(item);

                if (idExistente == null || idExistente == 0) {
                    item.setIdCatalogoCampo(null);
                }

                item = campoRepository.save(item);
            }

            objeto.setCatalogoCampo(itens);
        }
    }

    public void validarObjeto(Catalogo objeto) throws Exception {

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto), objeto.getIdTenant()),
                ID_FUNCTION);

    }

    public String sequencia(String idTenant) throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial(idTenant));

        return sq_sequencia;
    }

    @Transactional(rollbackFor = Exception.class)
    public void excluir(Long id) throws Exception {

        campoRepository.deleteByIdMestre(id);

        repository.deleteById(id);
    }
}
