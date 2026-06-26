package com.api_orcafacil.relatorio;

public abstract class RelatorioRequestBase {

    private String campoOrdenacao;
    private String direcaoOrdenacao;
    private String formatoSaida = "PDF";

    public String getCampoOrdenacao() { return campoOrdenacao; }
    public void setCampoOrdenacao(String campoOrdenacao) { this.campoOrdenacao = campoOrdenacao; }
    public String getDirecaoOrdenacao() { return direcaoOrdenacao; }
    public void setDirecaoOrdenacao(String direcaoOrdenacao) { this.direcaoOrdenacao = direcaoOrdenacao; }
    public String getFormatoSaida() { return formatoSaida; }
    public void setFormatoSaida(String formatoSaida) { this.formatoSaida = formatoSaida; }
}
