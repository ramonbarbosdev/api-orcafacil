package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.dto.dashboard.DashboardContagensDTO;
import com.api_orcafacil.dto.dashboard.DashboardOrcamentoRecenteDTO;
import com.api_orcafacil.dto.dashboard.DashboardResumoDTO;
import com.api_orcafacil.dto.dashboard.DashboardSerieMensalDTO;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.CatalogoRepository;
import com.api_orcafacil.repository.ClienteRepository;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.repository.ServicoRepository;

@Service
public class DashboardService {

    private static final int MESES_SERIE = 6;
    private static final int RECENTES_LIMITE = 8;

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final CatalogoRepository catalogoRepository;
    private final TenantContextService tenantContextService;

    public DashboardService(
            OrcamentoRepository orcamentoRepository,
            ClienteRepository clienteRepository,
            ServicoRepository servicoRepository,
            CatalogoRepository catalogoRepository,
            TenantContextService tenantContextService) {
        this.orcamentoRepository = orcamentoRepository;
        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
        this.catalogoRepository = catalogoRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(readOnly = true)
    public DashboardResumoDTO obterResumo() {
        Long idOrg = tenantContextService.idOrganizacaoObrigatoria();
        YearMonth mesAtual = YearMonth.now();
        YearMonth mesAnterior = mesAtual.minusMonths(1);

        Map<StatusOrcamento, Long> porStatus = mapearStatus(orcamentoRepository.contarPorStatus(idOrg));
        long total = porStatus.values().stream().mapToLong(Long::longValue).sum();
        long rascunhos = porStatus.getOrDefault(StatusOrcamento.RASCUNHO, 0L);
        long emAndamento = porStatus.getOrDefault(StatusOrcamento.GERADO, 0L)
                + porStatus.getOrDefault(StatusOrcamento.ENVIADO, 0L);
        long aprovados = porStatus.getOrDefault(StatusOrcamento.APROVADO, 0L);
        long rejeitados = porStatus.getOrDefault(StatusOrcamento.REJEITADO, 0L);

        BigDecimal faturamentoMes = orcamentoRepository.somarFaturamentoPorMes(
                idOrg, StatusOrcamento.APROVADO, mesAtual.getYear(), mesAtual.getMonthValue());
        BigDecimal faturamentoMesAnterior = orcamentoRepository.somarFaturamentoPorMes(
                idOrg, StatusOrcamento.APROVADO, mesAnterior.getYear(), mesAnterior.getMonthValue());

        long orcamentosMes = orcamentoRepository.contarPorMes(
                idOrg, mesAtual.getYear(), mesAtual.getMonthValue());
        long orcamentosMesAnterior = orcamentoRepository.contarPorMes(
                idOrg, mesAnterior.getYear(), mesAnterior.getMonthValue());

        DashboardResumoDTO resumo = new DashboardResumoDTO();
        resumo.setTotalOrcamentos(total);
        resumo.setRascunhos(rascunhos);
        resumo.setEmAndamento(emAndamento);
        resumo.setAprovados(aprovados);
        resumo.setRejeitados(rejeitados);
        resumo.setTotaisPorStatus(serializarStatus(porStatus));
        resumo.setFaturamentoAprovadoMes(faturamentoMes);
        resumo.setVariacaoFaturamentoMes(calcularVariacaoPercentual(faturamentoMes, faturamentoMesAnterior));
        resumo.setOrcamentosMes(orcamentosMes);
        resumo.setVariacaoOrcamentosMes(calcularVariacaoLong(orcamentosMes, orcamentosMesAnterior));
        resumo.setContagens(montarContagens(idOrg));
        resumo.setOrcamentosRecentes(montarRecentes(idOrg));
        resumo.setSerieMensal(montarSerieMensal(idOrg));
        return resumo;
    }

    private Map<StatusOrcamento, Long> mapearStatus(List<Object[]> linhas) {
        Map<StatusOrcamento, Long> mapa = new EnumMap<>(StatusOrcamento.class);
        for (Object[] linha : linhas) {
            mapa.put((StatusOrcamento) linha[0], (Long) linha[1]);
        }
        return mapa;
    }

    private Map<String, Long> serializarStatus(Map<StatusOrcamento, Long> porStatus) {
        Map<String, Long> serializado = new LinkedHashMap<>();
        for (StatusOrcamento status : StatusOrcamento.values()) {
            serializado.put(status.name(), porStatus.getOrDefault(status, 0L));
        }
        return serializado;
    }

    private DashboardContagensDTO montarContagens(Long idOrg) {
        DashboardContagensDTO contagens = new DashboardContagensDTO();
        contagens.setClientes(clienteRepository.countByIdOrganizacao(idOrg));
        contagens.setServicos(servicoRepository.countByIdOrganizacao(idOrg));
        contagens.setCatalogos(catalogoRepository.countByIdOrganizacao(idOrg));
        return contagens;
    }

    private List<DashboardOrcamentoRecenteDTO> montarRecentes(Long idOrg) {
        List<Orcamento> recentes = orcamentoRepository.findRecentesComCliente(
                idOrg, PageRequest.of(0, RECENTES_LIMITE));
        List<DashboardOrcamentoRecenteDTO> dtos = new ArrayList<>(recentes.size());
        for (Orcamento orcamento : recentes) {
            DashboardOrcamentoRecenteDTO dto = new DashboardOrcamentoRecenteDTO();
            dto.setIdOrcamento(orcamento.getIdOrcamento());
            dto.setNuOrcamento(orcamento.getNuOrcamento());
            dto.setNmCliente(orcamento.getNmCliente());
            dto.setVlPrecoFinal(orcamento.getVlPrecoFinal());
            dto.setDtEmissao(orcamento.getDtEmissao());
            dto.setDtValido(orcamento.getDtValido());
            dto.setTpStatus(orcamento.getTpStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<DashboardSerieMensalDTO> montarSerieMensal(Long idOrg) {
        LocalDate inicio = YearMonth.now().minusMonths(MESES_SERIE - 1L).atDay(1);
        List<Object[]> agregados = orcamentoRepository.agregarSerieMensal(idOrg, inicio);
        Map<YearMonth, DashboardSerieMensalDTO> mapa = new LinkedHashMap<>();

        for (int i = 0; i < MESES_SERIE; i++) {
            YearMonth mes = YearMonth.now().minusMonths(MESES_SERIE - 1L - i);
            DashboardSerieMensalDTO dto = new DashboardSerieMensalDTO();
            dto.setMes(formatarMes(mes));
            dto.setTotalOrcamentos(0);
            dto.setFaturamentoAprovado(BigDecimal.ZERO);
            mapa.put(mes, dto);
        }

        for (Object[] linha : agregados) {
            int ano = ((Number) linha[0]).intValue();
            int mes = ((Number) linha[1]).intValue();
            YearMonth chave = YearMonth.of(ano, mes);
            DashboardSerieMensalDTO dto = mapa.get(chave);
            if (dto == null) {
                continue;
            }
            dto.setTotalOrcamentos(((Number) linha[2]).longValue());
            dto.setFaturamentoAprovado((BigDecimal) linha[3]);
        }

        return new ArrayList<>(mapa.values());
    }

    private String formatarMes(YearMonth mes) {
        String abrev = mes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
        return abrev.substring(0, 1).toUpperCase() + abrev.substring(1) + "/" + String.valueOf(mes.getYear()).substring(2);
    }

    private BigDecimal calcularVariacaoPercentual(BigDecimal atual, BigDecimal anterior) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return atual.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return atual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(anterior, 1, RoundingMode.HALF_UP);
    }

    private Long calcularVariacaoLong(long atual, long anterior) {
        if (anterior == 0) {
            return atual > 0 ? 100L : 0L;
        }
        return Math.round(((atual - anterior) * 100.0) / anterior);
    }
}
