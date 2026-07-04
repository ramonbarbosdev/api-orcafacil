package com.api_orcafacil.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.orcamento.ClienteOrcamentoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoItemRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoPreviewPrecificacaoRequest;
import com.api_orcafacil.dto.orcamento.OrcamentoRequest;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.notificacao.service.OrcamentoNotificacaoService;
import com.api_orcafacil.repository.CatalogoRepository;
import com.api_orcafacil.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.repository.OrcamentoRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrcamentoServiceTest {

    private static final Long ID_ORG = 1L;
    private static final Long ID_CLIENTE = 10L;
    private static final Long ID_CONDICAO = 20L;
    private static final Long ID_METODO = 30L;
    private static final Long ID_CATALOGO = 40L;

    @Mock
    private OrcamentoRepository repository;
    @Mock
    private CatalogoRepository catalogoRepository;
    @Mock
    private CondicaoPagamentoRepository condicaoPagamentoRepository;
    @Mock
    private TenantContextService tenantContextService;
    @Mock
    private ClienteService clienteService;
    @Mock
    private ConfiguracaoOrcamentoService configuracaoOrcamentoService;
    @Mock
    private PrecificacaoService precificacaoService;
    @Mock
    private EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;
    @Mock
    private OrcamentoStatusHistoricoService statusHistoricoService;
    @Mock
    private ObjectProvider<PoliticaPlanoService> politicaPlanoService;
    @Mock
    private ObjectProvider<OrcamentoNotificacaoService> orcamentoNotificacaoService;
    @Mock
    private ObjectProvider<OrcamentoCentralSyncService> orcamentoCentralSyncService;

    @InjectMocks
    private OrcamentoService service;

    private EmpresaMetodoPrecificacao metodoSimples;

    @BeforeEach
    void setUp() {
        when(tenantContextService.idOrganizacaoObrigatoria()).thenReturn(ID_ORG);

        metodoSimples = new EmpresaMetodoPrecificacao();
        metodoSimples.setIdEmpresaMetodoPrecificacao(ID_METODO);
        MetodoPrecificacao metodo = new MetodoPrecificacao();
        metodo.setCdMetodoPrecificacao(TipoPrecificacao.SIMPLES);
        metodoSimples.setMetodoPrecificacao(metodo);

        when(empresaMetodoPrecificacaoService.obterEmpresaMetodoPrecificacaoSimples()).thenReturn(metodoSimples);
        when(empresaMetodoPrecificacaoService.buscarEntidadePorId(ID_METODO)).thenReturn(metodoSimples);
        when(precificacaoService.precificarItem(any(), eq(metodoSimples))).thenReturn(new BigDecimal("100.00"));
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @BeforeEach
        void mocksComuns() {
            when(clienteService.registrarClienteAPartirDoOrcamento(any())).thenReturn(ID_CLIENTE);
            when(condicaoPagamentoRepository.findByIdCondicaoPagamentoAndIdOrganizacao(ID_CONDICAO, ID_ORG))
                    .thenReturn(Optional.of(new CondicaoPagamento()));
            when(catalogoRepository.findByIdCatalogoAndIdOrganizacao(ID_CATALOGO, ID_ORG))
                    .thenReturn(Optional.of(new Catalogo()));
            when(repository.findByNuOrcamentoAndIdOrganizacao(any(), eq(ID_ORG))).thenReturn(Optional.empty());
        }

        @Test
        void novoOrcamentoSalvoComoGerado() {
            OrcamentoRequest request = requestValido(null);
            when(repository.save(any(Orcamento.class))).thenAnswer(inv -> {
                Orcamento o = inv.getArgument(0);
                o.setIdOrcamento(99L);
                return o;
            });

            var response = service.salvar(request);

            assertNotNull(response);
            assertEquals(StatusOrcamento.GERADO, response.getTpStatus());
            verify(repository).save(any(Orcamento.class));
            verify(statusHistoricoService).registrar(any(), eq(null), eq(StatusOrcamento.GERADO));
        }

        @Test
        void edicaoMantemStatusGerado() {
            Orcamento existente = orcamentoExistente(1L, StatusOrcamento.ENVIADO);
            when(repository.findByIdOrcamentoAndIdOrganizacao(1L, ID_ORG)).thenReturn(Optional.of(existente));
            when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = service.salvar(requestValido(1L));

            assertEquals(StatusOrcamento.GERADO, response.getTpStatus());
        }

        @Test
        void rejeitaSemItens() {
            OrcamentoRequest request = requestValido(null);
            request.setItens(List.of());

            assertThrows(BusinessException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaDataValidadeAnteriorEmissao() {
            OrcamentoRequest request = requestValido(null);
            request.setDtEmissao(LocalDate.of(2026, 7, 10));
            request.setDtValido(LocalDate.of(2026, 7, 1));

            assertThrows(BusinessException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaNumeroDuplicado() {
            OrcamentoRequest request = requestValido(null);
            Orcamento outro = orcamentoExistente(2L, StatusOrcamento.GERADO);
            when(repository.findByNuOrcamentoAndIdOrganizacao(request.getNuOrcamento(), ID_ORG))
                    .thenReturn(Optional.of(outro));

            assertThrows(ConflictException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaItemSemCatalogo() {
            OrcamentoRequest request = requestValido(null);
            request.getItens().get(0).setIdCatalogo(null);

            assertThrows(BusinessException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaItensDuplicados() {
            OrcamentoRequest request = requestValido(null);
            request.setItens(List.of(itemValido(), itemValido()));

            assertThrows(ConflictException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaQuantidadeInvalida() {
            OrcamentoRequest request = requestValido(null);
            request.getItens().get(0).setQtItem(BigDecimal.ZERO);

            assertThrows(BusinessException.class, () -> service.salvar(request));
        }

        @Test
        void rejeitaCondicaoPagamentoInexistente() {
            when(condicaoPagamentoRepository.findByIdCondicaoPagamentoAndIdOrganizacao(ID_CONDICAO, ID_ORG))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> service.salvar(requestValido(null)));
        }

        @Test
        void rejeitaCatalogoInexistente() {
            when(catalogoRepository.findByIdCatalogoAndIdOrganizacao(ID_CATALOGO, ID_ORG))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> service.salvar(requestValido(null)));
        }
    }

    @Nested
    @DisplayName("gerar")
    class Gerar {

        @BeforeEach
        void mocksComuns() {
            when(clienteService.registrarClienteAPartirDoOrcamento(any())).thenReturn(ID_CLIENTE);
            when(condicaoPagamentoRepository.findByIdCondicaoPagamentoAndIdOrganizacao(ID_CONDICAO, ID_ORG))
                    .thenReturn(Optional.of(new CondicaoPagamento()));
            when(catalogoRepository.findByIdCatalogoAndIdOrganizacao(ID_CATALOGO, ID_ORG))
                    .thenReturn(Optional.of(new Catalogo()));
            when(repository.findByNuOrcamentoAndIdOrganizacao(any(), eq(ID_ORG))).thenReturn(Optional.empty());
        }

        @Test
        void delegaParaSalvarComStatusGerado() {
            Orcamento existente = orcamentoExistente(5L, StatusOrcamento.GERADO);
            when(repository.findByIdOrcamentoAndIdOrganizacao(5L, ID_ORG)).thenReturn(Optional.of(existente));
            when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = service.gerar(5L, requestValido(5L));

            assertEquals(StatusOrcamento.GERADO, response.getTpStatus());
            verify(statusHistoricoService, never()).registrar(any(), any(), eq(StatusOrcamento.GERADO));
        }

        @Test
        void naoPersisteQuandoPrecificacaoFalha() {
            Orcamento existente = orcamentoExistente(5L, StatusOrcamento.GERADO);
            when(repository.findByIdOrcamentoAndIdOrganizacao(5L, ID_ORG)).thenReturn(Optional.of(existente));
            when(precificacaoService.precificarItem(any(), eq(metodoSimples)))
                    .thenThrow(new BusinessException("Metodo de precificacao nao definido"));

            assertThrows(BusinessException.class, () -> service.gerar(5L, requestValido(5L)));
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("alterarStatus")
    class AlterarStatus {

        @Test
        void permiteAlterarSemValidacaoDeTransicao() {
            Orcamento existente = orcamentoExistente(1L, StatusOrcamento.GERADO);
            when(repository.findByIdOrcamentoAndIdOrganizacao(1L, ID_ORG)).thenReturn(Optional.of(existente));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var response = service.alterarStatus(1L, StatusOrcamento.APROVADO);

            assertEquals(StatusOrcamento.APROVADO, response.getTpStatus());
        }
    }

    @Nested
    @DisplayName("previewPrecificacao")
    class PreviewPrecificacao {

        @Test
        void rejeitaItemSemCatalogo() {
            OrcamentoPreviewPrecificacaoRequest request = new OrcamentoPreviewPrecificacaoRequest();
            request.setItens(List.of(itemValido()));
            request.getItens().get(0).setIdCatalogo(null);

            assertThrows(BusinessException.class, () -> service.previewPrecificacao(request));
        }

        @Test
        void calculaTotal() {
            OrcamentoPreviewPrecificacaoRequest request = new OrcamentoPreviewPrecificacaoRequest();
            request.setItens(List.of(itemValido(), itemValido()));
            when(precificacaoService.precificarItem(any(), eq(metodoSimples))).thenReturn(new BigDecimal("50.00"));

            BigDecimal total = service.previewPrecificacao(request);

            assertEquals(new BigDecimal("100.00"), total);
        }
    }

    private OrcamentoRequest requestValido(Long idOrcamento) {
        OrcamentoRequest request = new OrcamentoRequest();
        request.setIdOrcamento(idOrcamento);
        request.setNuOrcamento("ORC-001");
        request.setDtEmissao(LocalDate.of(2026, 7, 1));
        request.setDtValido(LocalDate.of(2026, 7, 31));
        request.setIdCondicaoPagamento(ID_CONDICAO);
        request.setIdEmpresaMetodoPrecificacao(ID_METODO);
        request.setCliente(clienteValido());
        request.setItens(new ArrayList<>(List.of(itemValido())));
        return request;
    }

    private ClienteOrcamentoRequest clienteValido() {
        ClienteOrcamentoRequest cliente = new ClienteOrcamentoRequest();
        cliente.setNuCpfcnpj("12345678901");
        cliente.setNmCliente("Cliente Teste");
        cliente.setNuTelefone("11999999999");
        return cliente;
    }

    private OrcamentoItemRequest itemValido() {
        OrcamentoItemRequest item = new OrcamentoItemRequest();
        item.setIdCatalogo(ID_CATALOGO);
        item.setQtItem(BigDecimal.ONE);
        item.setVlCustoUnitario(new BigDecimal("80.00"));
        item.setVlPrecoUnitario(new BigDecimal("100.00"));
        return item;
    }

    private Orcamento orcamentoExistente(Long id, StatusOrcamento status) {
        Orcamento orcamento = new Orcamento();
        orcamento.setIdOrcamento(id);
        orcamento.setIdOrganizacao(ID_ORG);
        orcamento.setNuOrcamento("ORC-EXIST");
        orcamento.setDtEmissao(LocalDate.of(2026, 7, 1));
        orcamento.setDtValido(LocalDate.of(2026, 7, 31));
        orcamento.setIdCliente(ID_CLIENTE);
        orcamento.setIdCondicaoPagamento(ID_CONDICAO);
        orcamento.setTpStatus(status);
        orcamento.setVlPrecoBase(BigDecimal.TEN);
        orcamento.setVlPrecoFinal(BigDecimal.TEN);
        orcamento.setCdPublico("cd-publico-teste");

        OrcamentoItem item = new OrcamentoItem();
        item.setIdOrcamentoItem(100L);
        item.setIdCatalogo(ID_CATALOGO);
        item.setQtItem(BigDecimal.ONE);
        item.setVlCustoUnitario(new BigDecimal("80.00"));
        item.setVlPrecoUnitario(new BigDecimal("100.00"));
        item.setVlPrecoTotal(new BigDecimal("100.00"));
        item.setOrcamento(orcamento);
        orcamento.setItens(new ArrayList<>(List.of(item)));
        return orcamento;
    }
}
