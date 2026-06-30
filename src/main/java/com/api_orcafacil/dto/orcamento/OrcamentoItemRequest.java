package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoItemRequest {

    private Long idOrcamentoItem;

    @NotNull(message = "Catalogo do item nao informado")
    private Long idCatalogo;

    @NotNull(message = "Quantidade nao informada")
    @DecimalMin(value = "0.0001", message = "Quantidade invalida")
    private BigDecimal qtItem;

    @NotNull(message = "Custo unitario nao informado")
    @DecimalMin(value = "0.0", message = "Custo unitario invalido")
    private BigDecimal vlCustoUnitario;

    private List<OrcamentoItemCampoValor> camposValor = new ArrayList<>();

    public OrcamentoItem toEntity() {
        OrcamentoItem item = new OrcamentoItem();
        item.setIdCatalogo(idCatalogo);
        item.setQtItem(qtItem);
        item.setVlCustoUnitario(vlCustoUnitario);
        item.setCamposValor(copiarCamposValor());
        return item;
    }

    private List<OrcamentoItemCampoValor> copiarCamposValor() {
        if (camposValor == null || camposValor.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrcamentoItemCampoValor> copia = new ArrayList<>(camposValor.size());
        for (OrcamentoItemCampoValor origem : camposValor) {
            OrcamentoItemCampoValor campo = new OrcamentoItemCampoValor();
            campo.setIdCampoPersonalizado(origem.getIdCampoPersonalizado());
            campo.setTpValor(origem.getTpValor());
            campo.setVlInformado(origem.getVlInformado());
            campo.setDsDescricao(origem.getDsDescricao());
            copia.add(campo);
        }
        return copia;
    }
}