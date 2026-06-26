create sequence if not exists seq_configuracaoorcamento start with 100 increment by 1;

create table if not exists configuracao_orcamento (
    id_configuracaoorcamento bigint primary key default nextval('seq_configuracaoorcamento'),
    id_organizacao bigint not null,
    prefixo_numero varchar(10) not null,
    validade_dias integer not null,
    termos_padrao text,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create unique index if not exists ux_configuracao_orcamento_org on configuracao_orcamento (id_organizacao);
