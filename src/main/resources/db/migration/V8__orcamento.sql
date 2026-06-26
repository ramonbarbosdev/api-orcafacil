create sequence if not exists seq_orcamento start with 100 increment by 1;
create sequence if not exists seq_orcamento_item start with 100 increment by 1;
create sequence if not exists seq_orcamento_item_campo_valor start with 100 increment by 1;
create sequence if not exists seq_orcamento_status_historico start with 100 increment by 1;

create table if not exists orcamento (
    id_orcamento bigint primary key default nextval('seq_orcamento'),
    id_organizacao bigint not null,
    nu_orcamento varchar(50) not null,
    dt_emissao date not null,
    dt_valido date not null,
    id_cliente bigint not null references cliente (id_cliente),
    id_empresametodoprecificacao bigint references empresa_metodo_precificacao (id_empresametodoprecificacao),
    id_codicaopagamento bigint not null references codicao_pagamento (id_codicaopagamento),
    nu_prazoentrega integer not null default 20,
    ds_observacoes text,
    vl_precobase numeric(18, 4) not null,
    vl_precofinal numeric(18, 4),
    tp_status varchar(30) not null default 'RASCUNHO',
    cd_publico varchar(64) unique,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists orcamento_item (
    id_orcamentoitem bigint primary key default nextval('seq_orcamento_item'),
    id_orcamento bigint not null references orcamento (id_orcamento) on delete cascade,
    id_catalogo bigint not null references catalogo (id_catalogo),
    qt_item numeric(18, 4) not null,
    vl_custounitario numeric(18, 4) not null,
    vl_precounitario numeric(18, 4) not null,
    vl_precototal numeric(18, 4) not null,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists orcamento_item_campo_valor (
    id_orcamentoitemcampovalor bigint primary key default nextval('seq_orcamento_item_campo_valor'),
    id_orcamentoitem bigint not null references orcamento_item (id_orcamentoitem) on delete cascade,
    id_campopersonalizado bigint not null references campos_personalizados (id_campopersonalizado),
    tp_valor varchar(30) not null,
    vl_informado numeric(18, 4) not null,
    ds_descricao varchar(255),
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists orcamento_status_historico (
    id_orcamentostatushistorico bigint primary key default nextval('seq_orcamento_status_historico'),
    id_organizacao bigint not null,
    id_orcamento bigint not null references orcamento (id_orcamento) on delete cascade,
    tp_status_anterior varchar(30),
    tp_status_novo varchar(30) not null,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_orcamento_organizacao on orcamento (id_organizacao);
create index if not exists ix_orcamento_cd_publico on orcamento (cd_publico);
