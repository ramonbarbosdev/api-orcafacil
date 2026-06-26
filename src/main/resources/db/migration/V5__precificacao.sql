create sequence if not exists seq_campo_personalizado start with 100 increment by 1;
create sequence if not exists seq_empresametodoprecificacao start with 100 increment by 1;
create sequence if not exists seq_metodo_ajuste start with 100 increment by 1;

create table if not exists campos_personalizados (
    id_campopersonalizado bigint primary key default nextval('seq_campo_personalizado'),
    id_organizacao bigint not null,
    cd_campopersonalizado varchar(50) not null,
    nm_campopersonalizado varchar(100) not null,
    ds_campopersonalizado varchar(255),
    tp_campopersonalizado varchar(20) not null,
    tp_campovalor varchar(30) not null,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists empresa_metodo_precificacao (
    id_empresametodoprecificacao bigint primary key default nextval('seq_empresametodoprecificacao'),
    id_organizacao bigint not null,
    id_metodoprecificacao bigint not null references metodo_precificacao (id_metodoprecificacao),
    configuracao jsonb,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists metodo_ajustes (
    id_metodoajuste bigint primary key default nextval('seq_metodo_ajuste'),
    id_organizacao bigint not null,
    id_empresametodoprecificacao bigint not null references empresa_metodo_precificacao (id_empresametodoprecificacao),
    id_campopersonalizado bigint not null references campos_personalizados (id_campopersonalizado),
    tp_ajuste varchar(30) not null,
    tp_operacao varchar(30) not null,
    vl_condicao varchar(255),
    vl_incremento double precision not null,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_campos_personalizados_org on campos_personalizados (id_organizacao);
create index if not exists ix_empresa_metodo_org on empresa_metodo_precificacao (id_organizacao);
