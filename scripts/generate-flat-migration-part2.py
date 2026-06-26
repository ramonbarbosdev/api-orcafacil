#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "api_orcafacil"
FILES = {}

def add(p, c):
    FILES[p] = c

# REPOSITORIES
add("repository/ClienteRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Cliente;

public interface ClienteRepository extends TenantRepository<Cliente> {

    Optional<Cliente> findByIdClienteAndIdOrganizacao(Long idCliente, Long idOrganizacao);

    @Query(value = "SELECT * FROM cliente b WHERE b.nu_cpfcnpj = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Cliente> findByNuCpfcnpjAndIdOrganizacao(String nuCpfcnpj, Long idOrganizacao);
}
''')

add("repository/CatalogoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Catalogo;

public interface CatalogoRepository extends TenantRepository<Catalogo> {

    Optional<Catalogo> findByIdCatalogoAndIdOrganizacao(Long idCatalogo, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_catalogo AS BIGINT)), 0) FROM catalogo c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM catalogo b WHERE b.cd_catalogo = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Catalogo> findByCdCatalogoAndIdOrganizacao(String cdCatalogo, Long idOrganizacao);
}
''')

add("repository/CatalogoCampoRepository.java", '''package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CatalogoCampo;

public interface CatalogoCampoRepository extends JpaRepository<CatalogoCampo, Long> {

    List<CatalogoCampo> findByCatalogo_IdCatalogo(Long idCatalogo);

    @Modifying
    @Query("DELETE FROM CatalogoCampo c WHERE c.catalogo.idCatalogo = :idCatalogo")
    void deleteByIdCatalogo(Long idCatalogo);
}
''')

add("repository/CategoriaServicoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CategoriaServico;

public interface CategoriaServicoRepository extends TenantRepository<CategoriaServico> {

    Optional<CategoriaServico> findByIdCategoriaServicoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_categoriaservico AS BIGINT)), 0) FROM categoria_servico c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM categoria_servico b WHERE b.cd_categoriaservico = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CategoriaServico> findByCdCategoriaServicoAndIdOrganizacao(String cd, Long idOrganizacao);
}
''')

add("repository/ServicoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Servico;

public interface ServicoRepository extends TenantRepository<Servico> {

    Optional<Servico> findByIdServicoAndIdOrganizacao(Long idServico, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(s.cd_servico AS BIGINT)), 0) FROM servico s WHERE s.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM servico b WHERE b.cd_servico = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Servico> findByCdServicoAndIdOrganizacao(String cd, Long idOrganizacao);
}
''')

add("repository/MetodoPrecificacaoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;

public interface MetodoPrecificacaoRepository extends JpaRepository<MetodoPrecificacao, Long> {

    Optional<MetodoPrecificacao> findByCdMetodoPrecificacao(TipoPrecificacao cdMetodoPrecificacao);
}
''')

add("repository/CampoPersonalizadoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CampoPersonalizado;

public interface CampoPersonalizadoRepository extends TenantRepository<CampoPersonalizado> {

    Optional<CampoPersonalizado> findByIdCampoPersonalizadoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT * FROM campos_personalizados b WHERE b.cd_campopersonalizado = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CampoPersonalizado> findByCdCampoPersonalizadoAndIdOrganizacao(String cd, Long idOrganizacao);
}
''')

add("repository/EmpresaMetodoPrecificacaoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.EmpresaMetodoPrecificacao;

public interface EmpresaMetodoPrecificacaoRepository extends TenantRepository<EmpresaMetodoPrecificacao> {

    Optional<EmpresaMetodoPrecificacao> findByIdEmpresaMetodoPrecificacaoAndIdOrganizacao(Long id, Long idOrganizacao);

    Optional<EmpresaMetodoPrecificacao> findByIdOrganizacaoAndIdMetodoPrecificacao(Long idOrganizacao, Long idMetodoPrecificacao);

    @Query(value = "SELECT * FROM empresa_metodo_precificacao b WHERE b.id_metodoprecificacao = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<EmpresaMetodoPrecificacao> findByMetodoAndOrganizacao(Long idMetodoPrecificacao, Long idOrganizacao);
}
''')

add("repository/MetodoAjusteRepository.java", '''package com.api_orcafacil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.MetodoAjuste;

public interface MetodoAjusteRepository extends TenantRepository<MetodoAjuste> {

    Optional<MetodoAjuste> findByIdMetodoAjusteAndIdOrganizacao(Long id, Long idOrganizacao);

    List<MetodoAjuste> findByIdOrganizacao(Long idOrganizacao);

    @Query(value = "SELECT * FROM metodo_ajustes b WHERE b.id_campopersonalizado = ?1 LIMIT 1", nativeQuery = true)
    Optional<MetodoAjuste> findByIdCampoPersonalizado(Long idCampoPersonalizado);
}
''')

add("repository/CondicaoPagamentoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CondicaoPagamento;

public interface CondicaoPagamentoRepository extends TenantRepository<CondicaoPagamento> {

    Optional<CondicaoPagamento> findByIdCondicaoPagamentoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_codicaopagamento AS BIGINT)), 0) FROM codicao_pagamento c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM codicao_pagamento b WHERE b.cd_codicaopagamento = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CondicaoPagamento> findByCdCondicaoPagamentoAndIdOrganizacao(String cd, Long idOrganizacao);
}
''')

add("repository/ConfiguracaoOrcamentoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import com.api_orcafacil.model.ConfiguracaoOrcamento;

public interface ConfiguracaoOrcamentoRepository extends TenantRepository<ConfiguracaoOrcamento> {

    Optional<ConfiguracaoOrcamento> findByIdOrganizacao(Long idOrganizacao);
}
''')

add("repository/OrcamentoRepository.java", '''package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Orcamento;

public interface OrcamentoRepository extends TenantRepository<Orcamento> {

    Optional<Orcamento> findByIdOrcamentoAndIdOrganizacao(Long idOrcamento, Long idOrganizacao);

    Optional<Orcamento> findByCdPublico(String cdPublico);

    @Query(value = """
            SELECT COALESCE(
              MAX(CAST(SUBSTRING(c.nu_orcamento FROM '[0-9]+') AS BIGINT)),
              0
            )
            FROM orcamento c
            WHERE c.id_organizacao = ?1
            """, nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM orcamento b WHERE b.nu_orcamento = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Orcamento> findByNuOrcamentoAndIdOrganizacao(String nuOrcamento, Long idOrganizacao);
}
''')

add("repository/OrcamentoItemRepository.java", '''package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoItem;

public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Long> {

    List<OrcamentoItem> findByOrcamento_IdOrcamento(Long idOrcamento);

    @Modifying
    @Query("DELETE FROM OrcamentoItem i WHERE i.orcamento.idOrcamento = :idOrcamento")
    void deleteByIdOrcamento(Long idOrcamento);
}
''')

add("repository/OrcamentoItemCampoValorRepository.java", '''package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoItemCampoValor;

public interface OrcamentoItemCampoValorRepository extends JpaRepository<OrcamentoItemCampoValor, Long> {

    List<OrcamentoItemCampoValor> findByOrcamentoItem_IdOrcamentoItem(Long idOrcamentoItem);

    @Modifying
    @Query("DELETE FROM OrcamentoItemCampoValor c WHERE c.orcamentoItem.idOrcamentoItem = :idOrcamentoItem")
    void deleteByIdOrcamentoItem(Long idOrcamentoItem);
}
''')

add("repository/OrcamentoStatusHistoricoRepository.java", '''package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoStatusHistorico;

public interface OrcamentoStatusHistoricoRepository extends TenantRepository<OrcamentoStatusHistorico> {

    List<OrcamentoStatusHistorico> findByIdOrcamentoOrderByDtCriacaoAsc(Long idOrcamento);

    @Modifying
    @Query("DELETE FROM OrcamentoStatusHistorico h WHERE h.idOrcamento = :idOrcamento")
    void deleteByIdOrcamento(Long idOrcamento);
}
''')

# UTIL
add("common/SequenciaUtil.java", '''package com.api_orcafacil.common;

public final class SequenciaUtil {

    private SequenciaUtil() {
    }

    public static String gerarSequencia(Long sequencia) {
        long ultima = sequencia != null ? sequencia : 0L;
        return "%03d".formatted(ultima + 1);
    }
}
''')

if __name__ == "__main__":
    for rel, content in FILES.items():
        path = ROOT / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        print(f"Wrote {rel}")
    print(f"Total: {len(FILES)}")
