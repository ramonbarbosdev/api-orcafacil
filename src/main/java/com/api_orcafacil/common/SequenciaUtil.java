package com.api_orcafacil.common;

public final class SequenciaUtil {

    private SequenciaUtil() {
    }

    public static String gerarSequencia(Long sequencia) {
        long ultima = sequencia != null ? sequencia : 0L;
        return "%03d".formatted(ultima + 1);
    }
}
