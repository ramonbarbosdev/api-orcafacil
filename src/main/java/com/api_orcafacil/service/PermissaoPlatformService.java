package com.api_orcafacil.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.PermissaoItemDTO;
import com.api_orcafacil.dto.PermissaoModuloDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.repository.central.CentralPapelPermissaoPadraoRepository;
import com.api_orcafacil.repository.central.CentralPapelRepository;
import com.api_orcafacil.repository.central.CentralPermissaoGlobalRepository;
import com.api_orcafacil.repository.central.CentralPlanoPermissaoRepository;
import com.api_orcafacil.tenant.central.model.CentralPapelPermissaoPadrao;
import com.api_orcafacil.tenant.central.model.CentralPermissaoGlobal;
import com.api_orcafacil.tenant.central.model.CentralPlanoPermissao;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
@RequiredArgsConstructor
public class PermissaoPlatformService {

    private static final Map<String, String> ROTULOS_MODULO = Map.ofEntries(
            Map.entry("clientes", "Clientes"),
            Map.entry("catalogos", "Catálogos"),
            Map.entry("servicos", "Serviços"),
            Map.entry("categorias-servico", "Categorias de serviço"),
            Map.entry("orcamentos", "Orçamentos"),
            Map.entry("condicoes-pagamento", "Condições de pagamento"),
            Map.entry("configuracao-orcamento", "Configuração de orçamento"),
            Map.entry("metodos-precificacao", "Métodos de precificação"),
            Map.entry("campos-personalizados", "Campos personalizados"),
            Map.entry("metodos-ajuste", "Métodos de ajuste"),
            Map.entry("empresa-metodos-precificacao", "Métodos da empresa"),
            Map.entry("perfil", "Perfil"));

    private final CentralPermissaoGlobalRepository permissaoRepository;
    private final CentralPapelRepository papelRepository;
    private final CentralPapelPermissaoPadraoRepository papelPermissaoPadraoRepository;
    private final CentralPlanoPermissaoRepository planoPermissaoRepository;

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<PermissaoModuloDTO> listarCatalogo() {
        List<PermissaoItemDTO> itens = permissaoRepository.findByFlAtivoTrueOrderByNmChaveAsc().stream()
                .map(this::toItem)
                .toList();

        LinkedHashMap<String, List<PermissaoItemDTO>> porModulo = new LinkedHashMap<>();
        for (PermissaoItemDTO item : itens) {
            String modulo = moduloDaChave(item.nmChave());
            porModulo.computeIfAbsent(modulo, k -> new ArrayList<>()).add(item);
        }

        return porModulo.entrySet().stream()
                .map(e -> new PermissaoModuloDTO(
                        e.getKey(),
                        rotuloModulo(e.getKey()),
                        List.copyOf(e.getValue())))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<String> listarChavesAtivas() {
        return permissaoRepository.findByFlAtivoTrueOrderByNmChaveAsc().stream()
                .map(CentralPermissaoGlobal::getNmChave)
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<Long> resolverIdsPorChaves(List<String> chaves) {
        if (chaves == null || chaves.isEmpty()) {
            return List.of();
        }
        Set<String> unicas = chaves.stream()
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (unicas.isEmpty()) {
            return List.of();
        }

        List<Long> ids = permissaoRepository.findByNmChaveInAndFlAtivoTrueOrderByIdPermissaoAsc(unicas).stream()
                .map(CentralPermissaoGlobal::getIdPermissao)
                .toList();

        if (ids.size() != unicas.size()) {
            throw new BusinessException("Uma ou mais permissoes informadas sao invalidas");
        }
        return ids;
    }

    void substituirPermissoesPapel(Long idPapel, List<String> chaves) {
        validarPapel(idPapel);
        List<Long> ids = resolverIdsPorChaves(chaves);

        papelPermissaoPadraoRepository.deleteByIdPapel(idPapel);
        salvarVinculosPapel(idPapel, ids);
    }

    void substituirPermissoesPlano(Long idPlano, List<String> chaves) {
        List<Long> ids = resolverIdsPorChaves(chaves);

        planoPermissaoRepository.deleteByIdPlanoAssinatura(idPlano);
        salvarVinculosPlano(idPlano, ids);
    }

    void concederTodasPermissoesPlano(Long idPlano) {
        List<Long> ids = permissaoRepository.findByFlAtivoTrueOrderByNmChaveAsc().stream()
                .map(CentralPermissaoGlobal::getIdPermissao)
                .toList();
        salvarVinculosPlano(idPlano, ids);
    }

    private void salvarVinculosPapel(Long idPapel, List<Long> idsPermissao) {
        List<CentralPapelPermissaoPadrao> vinculos = idsPermissao.stream()
                .map(idPermissao -> new CentralPapelPermissaoPadrao(idPapel, idPermissao))
                .toList();
        papelPermissaoPadraoRepository.saveAll(vinculos);
    }

    private void salvarVinculosPlano(Long idPlano, List<Long> idsPermissao) {
        List<CentralPlanoPermissao> vinculos = idsPermissao.stream()
                .map(idPermissao -> new CentralPlanoPermissao(idPlano, idPermissao))
                .toList();
        planoPermissaoRepository.saveAll(vinculos);
    }

    private void validarPapel(Long idPapel) {
        if (!papelRepository.existsByIdPapelAndFlAtivoTrue(idPapel)) {
            throw new BusinessException("Papel nao encontrado");
        }
    }

    private PermissaoItemDTO toItem(CentralPermissaoGlobal permissao) {
        String chave = permissao.getNmChave();
        int idx = chave.lastIndexOf('.');
        String acao = idx >= 0 ? chave.substring(idx + 1) : chave;
        return new PermissaoItemDTO(
                permissao.getIdPermissao(),
                permissao.getNmPermissao(),
                chave,
                acao);
    }

    static String moduloDaChave(String chave) {
        int idx = chave.lastIndexOf('.');
        return idx >= 0 ? chave.substring(0, idx) : chave;
    }

    static String rotuloModulo(String modulo) {
        if (ROTULOS_MODULO.containsKey(modulo)) {
            return ROTULOS_MODULO.get(modulo);
        }
        String legivel = modulo.replace('-', ' ');
        if (legivel.isEmpty()) {
            return modulo;
        }
        return Character.toUpperCase(legivel.charAt(0)) + legivel.substring(1);
    }
}
