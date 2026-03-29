package com.api_orcafacil.domain.relatorio.dto;


public abstract class RelatorioRequestBase {
 
    
      // private LocalDate dataInicial;
    // private LocalDate dataFinal;

    // Campos Comuns de Ordenação
    private String campoOrdenacao; // Ex: "dataCriacao", "valorTotal"
    private String direcaoOrdenacao; // Ex: "ASC", "DESC"

    private String formatoSaida = "PDF"; // Padrão: PDF

    public String getCampoOrdenacao() {
        return campoOrdenacao;
    }   

    public void setCampoOrdenacao(String campoOrdenacao) {
        this.campoOrdenacao = campoOrdenacao;
    }

    public String getDirecaoOrdenacao() {
        return direcaoOrdenacao;
    }

    public void setDirecaoOrdenacao(String direcaoOrdenacao) {
        this.direcaoOrdenacao = direcaoOrdenacao;
    }

    public String getFormatoSaida() {
        return formatoSaida;
    }

    public void setFormatoSaida(String formatoSaida) {
        this.formatoSaida = formatoSaida;
    }


}
