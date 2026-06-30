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
import com.api_orcafacil.dto.permissao.CatalogoRecursoCuradoDTO;
import com.api_orcafacil.dto.permissao.CatalogoRecursoItemDTO;
import com.api_orcafacil.dto.permissao.PermissaoDetalheDTO;
import com.api_orcafacil.dto.permissao.PermissaoItemRequestDTO;
import com.api_orcafacil.dto.permissao.PermissaoItemUpdateDTO;
import com.api_orcafacil.dto.permissao.RegistrarRecursoRequestDTO;
import com.api_orcafacil.dto.permissao.RegistrarRecursoResponseDTO;
import com.api_orcafacil.dto.permissao.RecursoDescobertoDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralPapelPermissaoPadraoRepository;
import com.api_orcafacil.repository.central.CentralPapelRepository;
import com.api_orcafacil.repository.central.CentralPermissaoGlobalRepository;
import com.api_orcafacil.repository.central.CentralPlanoPermissaoRepository;
import com.api_orcafacil.security.permissao.PermissaoCatalogoCurado;
import com.api_orcafacil.security.permissao.PermissaoNormalizador;
import com.api_orcafacil.security.permissao.RotaPermissaoScanner;
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
    private static final List<String> ACOES_CRUD = List.of("exibir", "ler", "criar", "editar", "deletar");
    private static final List<String> ORDEM_ACOES = List.of("exibir", "ler", "criar", "editar", "deletar");

    private static final Map<String, String> ROTULOS_MODULO = new LinkedHashMap<>(PermissaoCatalogoCurado.rotulosPorModulo());

    private final CentralPermissaoGlobalRepository permissaoRepository;
    private final CentralPapelRepository papelRepository;
    private final CentralPapelPermissaoPadraoRepository papelPermissaoPadraoRepository;
    private final CentralPlanoPermissaoRepository planoPermissaoRepository;
    private final RotaPermissaoScanner rotaPermissaoScanner;

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
    public List<CatalogoRecursoItemDTO> listarCatalogoRecursos() {
        Map<String, List<String>> existentesPorModulo = permissoesAtivasPorModulo();
        LinkedHashMap<String, CatalogoRecursoItemDTO> uniao = new LinkedHashMap<>();

        for (CatalogoRecursoCuradoDTO curado : PermissaoCatalogoCurado.listar()) {
            uniao.put(curado.modulo(), montarItemCatalogo(curado, existentesPorModulo, "CATALOGO", true));
        }

        for (RecursoDescobertoDTO descoberto : rotaPermissaoScanner.descobrir()) {
            if (uniao.containsKey(descoberto.modulo())) {
                continue;
            }
            CatalogoRecursoCuradoDTO sintetico = new CatalogoRecursoCuradoDTO(
                    descoberto.modulo(),
                    rotuloModulo(descoberto.modulo()),
                    descoberto.rota(),
                    "Descoberto",
                    ACOES_CRUD);
            uniao.put(descoberto.modulo(), montarItemCatalogo(sintetico, existentesPorModulo, "DESCOBERTO", false));
        }

        return uniao.values().stream()
                .sorted(Comparator.comparing(CatalogoRecursoItemDTO::grupo)
                        .thenComparing(CatalogoRecursoItemDTO::modulo))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<CatalogoRecursoItemDTO> listarCatalogoSugeridos() {
        return listarCatalogoRecursos().stream()
                .filter(item -> !item.noCatalogoCurado() || !"COMPLETO".equals(item.status()))
                .toList();
    }

    public RegistrarRecursoResponseDTO registrarRecurso(RegistrarRecursoRequestDTO request) {
        String modulo = PermissaoNormalizador.normalizarModulo(request.recurso());
        validarCodigoModulo(modulo);
        validarRecursoConhecido(modulo);

        List<String> acoes = resolverAcoesRegistro(modulo, request.acoes());
        String nmModulo = request.descricao() != null && !request.descricao().isBlank()
                ? request.descricao().trim()
                : rotuloModulo(modulo);

        List<String> criadas = new ArrayList<>();
        List<String> jaExistentes = new ArrayList<>();

        for (String acao : acoes) {
            String chave = PermissaoNormalizador.montarChave(modulo, acao);
            if (permissaoRepository.existsByNmChave(chave)) {
                jaExistentes.add(chave);
                continue;
            }
            CentralPermissaoGlobal permissao = new CentralPermissaoGlobal();
            permissao.setNmChave(chave);
            permissao.setNmPermissao(rotuloPermissao(nmModulo, acao));
            permissao.setFlAtivo(true);
            permissaoRepository.save(permissao);
            criadas.add(chave);
        }

        return new RegistrarRecursoResponseDTO(modulo, criadas, jaExistentes);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<PermissaoDetalheDTO> listarPermissoesDetalhadas() {
        return permissaoRepository.findAllByOrderByNmChaveAsc().stream()
                .map(this::toDetalhe)
                .toList();
    }

    public PermissaoDetalheDTO criarPermissao(PermissaoItemRequestDTO request) {
        String modulo = PermissaoNormalizador.normalizarModulo(request.modulo());
        String acao = PermissaoNormalizador.normalizarAcao(request.acao());
        validarCodigoModulo(modulo);
        String chave = PermissaoNormalizador.montarChave(modulo, acao);
        if (permissaoRepository.existsByNmChave(chave)) {
            throw new ConflictException("Permissao ja cadastrada: " + chave);
        }

        CentralPermissaoGlobal permissao = new CentralPermissaoGlobal();
        permissao.setNmChave(chave);
        permissao.setNmPermissao(request.descricao() != null && !request.descricao().isBlank()
                ? request.descricao().trim()
                : rotuloPermissao(rotuloModulo(modulo), acao));
        permissao.setFlAtivo(true);
        return toDetalhe(permissaoRepository.save(permissao));
    }

    public PermissaoDetalheDTO atualizarPermissao(Long id, PermissaoItemUpdateDTO request) {
        CentralPermissaoGlobal permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissao nao encontrada"));
        permissao.setNmPermissao(request.descricao().trim());
        if (request.flAtivo() != null) {
            permissao.setFlAtivo(request.flAtivo());
        }
        return toDetalhe(permissaoRepository.save(permissao));
    }

    public void desativarPermissao(Long id) {
        CentralPermissaoGlobal permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissao nao encontrada"));
        permissao.setFlAtivo(false);
        permissaoRepository.save(permissao);
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
        return PermissaoNormalizador.moduloDaChave(chave);
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

    private Map<String, List<String>> permissoesAtivasPorModulo() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        for (CentralPermissaoGlobal permissao : permissaoRepository.findByFlAtivoTrueOrderByNmChaveAsc()) {
            String modulo = moduloDaChave(permissao.getNmChave());
            mapa.computeIfAbsent(modulo, k -> new ArrayList<>()).add(permissao.getNmChave());
        }
        return mapa;
    }

    private CatalogoRecursoItemDTO montarItemCatalogo(
            CatalogoRecursoCuradoDTO base,
            Map<String, List<String>> existentesPorModulo,
            String origem,
            boolean noCatalogoCurado) {
        List<String> sugeridas = base.acoesSugeridas();
        List<String> existentes = existentesPorModulo.getOrDefault(base.modulo(), List.of()).stream()
                .filter(chave -> sugeridas.contains(acaoDaChave(chave)))
                .sorted()
                .toList();
        String status = calcularStatus(sugeridas, existentes);
        boolean cadastrado = "COMPLETO".equals(status);
        return new CatalogoRecursoItemDTO(
                base.modulo(),
                base.label(),
                base.rota(),
                base.grupo(),
                origem,
                cadastrado,
                noCatalogoCurado,
                status,
                sugeridas,
                existentes);
    }

    private String calcularStatus(List<String> sugeridas, List<String> existentes) {
        if (existentes.isEmpty()) {
            return "PENDENTE";
        }
        long faltantes = sugeridas.stream()
                .filter(acao -> existentes.stream().noneMatch(chave -> chave.endsWith("." + acao)))
                .count();
        if (faltantes == 0) {
            return "COMPLETO";
        }
        return "PARCIAL";
    }

    private List<String> resolverAcoesRegistro(String modulo, List<String> acoesRequest) {
        if (acoesRequest != null && !acoesRequest.isEmpty()) {
            return acoesRequest.stream()
                    .map(PermissaoNormalizador::normalizarAcao)
                    .distinct()
                    .toList();
        }
        return PermissaoCatalogoCurado.buscar(modulo)
                .map(CatalogoRecursoCuradoDTO::acoesSugeridas)
                .orElse(ACOES_CRUD);
    }

    private void validarRecursoConhecido(String modulo) {
        boolean conhecido = PermissaoCatalogoCurado.buscar(modulo).isPresent()
                || rotaPermissaoScanner.descobrir().stream().anyMatch(r -> r.modulo().equals(modulo))
                || permissaoRepository.existsByNmChaveStartingWith(modulo + ".");
        if (!conhecido) {
            throw new BusinessException(
                    "Recurso nao encontrado no catalogo nem descoberto pelos controllers: " + modulo);
        }
    }

    private PermissaoDetalheDTO toDetalhe(CentralPermissaoGlobal permissao) {
        return new PermissaoDetalheDTO(
                permissao.getIdPermissao(),
                permissao.getNmChave(),
                moduloDaChave(permissao.getNmChave()),
                acaoDaChave(permissao.getNmChave()),
                permissao.getNmPermissao(),
                permissao.isFlAtivo());
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
