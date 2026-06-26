create sequence if not exists seq_catalogo start with 100 increment by 1;
create sequence if not exists seq_catalogo_campo start with 100 increment by 1;

create table if not exists catalogo (
    id_catalogo bigint primary key default nextval('seq_catalogo'),
    id_organizacao bigint not null,
    tp_item varchar(30),
    cd_catalogo varchar(50) not null,
    nm_catalogo varchar(255) not null,
    ds_catalogo text,
    vl_custobase numeric(18, 4),
    vl_precobase numeric(18, 4),
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists catalogo_campo (
    id_catalogo_campo bigint primary key default nextval('seq_catalogo_campo'),
    id_catalogo bigint not null references catalogo (id_catalogo),
    id_campopersonalizado bigint not null,
    vl_padrao varchar(255),
    ds_descricao varchar(255),
    fl_editavel boolean not null default true,
    ordem integer,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_catalogo_organizacao on catalogo (id_organizacao);
