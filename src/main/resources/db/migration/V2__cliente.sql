create sequence if not exists seq_cliente start with 100 increment by 1;

create table if not exists cliente (
    id_cliente bigint primary key default nextval('seq_cliente'),
    id_organizacao bigint not null,
    tp_cliente varchar(20),
    nu_cpfcnpj varchar(20) not null,
    nm_cliente varchar(255) not null,
    ds_email varchar(255),
    nu_telefone varchar(30),
    nu_cep varchar(20),
    ds_logradouro varchar(255),
    ds_complemento varchar(255),
    ds_bairro varchar(120),
    ds_cidade varchar(120),
    ds_estado varchar(2),
    fl_ativo boolean not null default true,
    ds_observacoes text,
    id_usuario bigint,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_cliente_organizacao on cliente (id_organizacao);
