create table if not exists orcamento_publico (
    cd_publico varchar(64) primary key,
    id_organizacao bigint not null references organizacao (id_organizacao),
    id_orcamento bigint not null,
    dt_criacao timestamp not null default now()
);

create index if not exists ix_orcamento_publico_org on orcamento_publico (id_organizacao);
