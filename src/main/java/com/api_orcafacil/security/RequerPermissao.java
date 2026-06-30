package com.api_orcafacil.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override explícito da permissão derivada por URL + método HTTP.
 * Ex.: {@code @RequerPermissao(modulo = "orcamentos", acao = "ler")}
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequerPermissao {

    String modulo();

    String acao();
}
