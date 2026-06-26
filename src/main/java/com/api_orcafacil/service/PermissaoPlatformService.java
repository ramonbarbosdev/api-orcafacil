package com.api_orcafacil.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.ModuloPermissaoAdminDTO;
import com.api_orcafacil.dto.ModuloPermissaoRequestDTO;
import com.api_orcafacil.dto.ModuloPermissaoUpdateDTO;
import com.api_orcafacil.dto.PermissaoItemDTO;
import com.api_orcafacil.dto.PermissaoModuloDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
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

    private static final Set<String> MODULOS_RESERVADOS = Set.of("auth", "admin", "error");
    private static final List<String> ACOES_PADRAO = List.of("exibir", "ler", "criar", "editar", "deletar");
    private static final List<String> ORDEM_ACOES = List.of("exibir", "ler", "criar", "editar", "deletar");

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
                        ordenarItens(e.getValue())))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<ModuloPermissaoAdminDTO> listarModulos() {
        LinkedHashMap<String, List<CentralPermissaoGlobal>> porModulo = new LinkedHashMap<>();
        for (CentralPermissaoGlobal permissao : permissaoRepository.findAllByOrderByNmChaveAsc()) {
            String modulo = moduloDaChave(permissao.getNmChave());
            porModulo.computeIfAbsent(modulo, k -> new ArrayList<>()).add(permissao);
        }
        return porModulo.entrySet().stream()
                .map(e -> toModuloAdmin(e.getKey(), e.getValue()))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public ModuloPermissaoAdminDTO buscarModulo(String modulo) {
        List<CentralPermissaoGlobal> permissoes = buscarPermissoesModulo(modulo);
        if (permissoes.isEmpty()) {
            throw new ResourceNotFoundException("Modulo nao encontrado");
        }
        return toModuloAdmin(modulo, permissoes);
    }

    public ModuloPermissaoAdminDTO criarModulo(ModuloPermissaoRequestDTO request) {
        String codigo = request.codigoModulo().trim().toLowerCase();
        validarCodigoModulo(codigo);
        if (permissaoRepository.existsByNmChaveStartingWith(codigo + ".")) {
            throw new ConflictException("Modulo ja cadastrado: " + codigo);
        }

        String nmModulo = request.nmModulo().trim();
        for (String acao : ACOES_PADRAO) {
            CentralPermissaoGlobal permissao = new CentralPermissaoGlobal();
            permissao.setNmChave(codigo + "." + acao);
            permissao.setNmPermissao(rotuloPermissao(nmModulo, acao));
            permissao.setFlAtivo(true);
            permissaoRepository.save(permissao);
        }
        return buscarModulo(codigo);
    }

    public ModuloPermissaoAdminDTO atualizarModulo(String modulo, ModuloPermissaoUpdateDTO request) {
        List<CentralPermissaoGlobal> permissoes = buscarPermissoesModulo(modulo);
        if (permissoes.isEmpty()) {
            throw new ResourceNotFoundException("Modulo nao encontrado");
        }

        String nmModulo = request.nmModulo().trim();
        boolean ativo = Boolean.TRUE.equals(request.flAtivo());
        for (CentralPermissaoGlobal permissao : permissoes) {
            permissao.setNmPermissao(rotuloPermissao(nmModulo, acaoDaChave(permissao.getNmChave())));
            permissao.setFlAtivo(ativo);
        }
        permissaoRepository.saveAll(permissoes);
        return buscarModulo(modulo);
    }

    public void desativarModulo(String modulo) {
        ModuloPermissaoAdminDTO atual = buscarModulo(modulo);
        atualizarModulo(modulo, new ModuloPermissaoUpdateDTO(atual.nmModulo(), false));
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

    private ModuloPermissaoAdminDTO toModuloAdmin(String modulo, List<CentralPermissaoGlobal> permissoes) {
        boolean ativo = permissoes.stream().allMatch(CentralPermissaoGlobal::isFlAtivo);
        String nmModulo = permissoes.stream()
                .filter(p -> p.getNmChave().endsWith(".ler"))
                .findFirst()
                .map(p -> extrairNomeRecurso(p.getNmPermissao()))
                .orElseGet(() -> rotuloModulo(modulo));
        List<PermissaoItemDTO> itens = ordenarItens(permissoes.stream().map(this::toItem).toList());
        return new ModuloPermissaoAdminDTO(modulo, nmModulo, ativo, permissoes.size(), itens);
    }

    private List<CentralPermissaoGlobal> buscarPermissoesModulo(String modulo) {
        return permissaoRepository.findByNmChaveStartingWith(modulo + ".");
    }

    private void validarCodigoModulo(String codigo) {
        if (!codigo.matches("^[a-z][a-z0-9-]*$")) {
            throw new BusinessException("Codigo do modulo invalido. Use letras minusculas, numeros e hifen.");
        }
        if (MODULOS_RESERVADOS.contains(codigo)) {
            throw new BusinessException("Codigo reservado: " + codigo);
        }
    }

    private String rotuloPermissao(String nmModulo, String acao) {
        String nome = nmModulo.trim();
        return switch (acao) {
            case "exibir" -> "Exibir " + nome + " no menu";
            case "ler" -> "Listar " + nome;
            case "criar" -> "Criar " + nome;
            case "editar" -> "Editar " + nome;
            case "deletar" -> "Deletar " + nome;
            default -> nome + " " + acao;
        };
    }

    private List<PermissaoItemDTO> ordenarItens(List<PermissaoItemDTO> itens) {
        return itens.stream()
                .sorted(Comparator.comparingInt(item -> ordemAcao(item.acao())))
                .toList();
    }

    private int ordemAcao(String acao) {
        int idx = ORDEM_ACOES.indexOf(acao);
        return idx >= 0 ? idx : 99;
    }

    private String acaoDaChave(String chave) {
        int idx = chave.lastIndexOf('.');
        return idx >= 0 ? chave.substring(idx + 1) : chave;
    }

    private String extrairNomeRecurso(String nmPermissao) {
        if (nmPermissao != null && nmPermissao.length() > 7
                && nmPermissao.regionMatches(true, 0, "Listar ", 0, 7)) {
            String nome = nmPermissao.substring(7).trim();
            if (!nome.isEmpty()) {
                return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
            }
        }
        return nmPermissao;
    }
}
