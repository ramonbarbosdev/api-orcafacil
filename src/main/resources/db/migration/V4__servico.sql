create sequence if not exists seq_servico start with 100 increment by 1;
create sequence if not exists seq_categoriaservico start with 100 increment by 1;

create table if not exists categoria_servico (
    id_categoriaservico bigint primary key default nextval('seq_categoriaservico'),
    id_organizacao bigint not null,
    cd_categoriaservico varchar(50) not null,
    nm_categoriaservico varchar(255) not null,
    ds_categoriaservico text,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create table if not exists servico (
    id_servico bigint primary key default nextval('seq_servico'),
    id_organizacao bigint not null,
    id_categoriaservico bigint references categoria_servico (id_categoriaservico),
    cd_servico varchar(50) not null,
    nm_servico varchar(255) not null,
    ds_servico text,
    vl_custo numeric(18, 4),
    vl_preco numeric(18, 4),
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_servico_organizacao on servico (id_organizacao);
create index if not exists ix_categoria_servico_organizacao on categoria_servico (id_organizacao);
