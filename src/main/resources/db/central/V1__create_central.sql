create sequence if not exists seq_central_organizacao start with 100 increment by 1;
create sequence if not exists seq_central_usuario start with 100 increment by 1;
create sequence if not exists seq_central_usuario_organizacao start with 100 increment by 1;
create sequence if not exists seq_central_permissao start with 100 increment by 1;
create sequence if not exists seq_central_papel start with 100 increment by 1;
create sequence if not exists seq_central_papel_permissao start with 100 increment by 1;
create sequence if not exists seq_central_usuario_permissao start with 100 increment by 1;
create sequence if not exists seq_central_provisionamento start with 100 increment by 1;

create table if not exists organizacao (
    id_organizacao bigint primary key default nextval('seq_central_organizacao'),
    slug varchar(120) not null,
    nm_organizacao varchar(255) not null,
    ds_documento varchar(50),
    status varchar(40) not null default 'EM_PROVISIONAMENTO',
    database_name varchar(120) not null,
    storage_mode varchar(40) not null default 'DATABASE_PER_ORG',
    fl_ativo boolean not null default true,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now(),
    constraint ck_organizacao_status
        check (status in ('EM_PROVISIONAMENTO', 'PROVISIONAMENTO_FALHOU', 'ATIVA', 'SUSPENSA', 'CANCELADA'))
);

create unique index if not exists ux_organizacao_slug on organizacao (slug);
create unique index if not exists ux_organizacao_database_name on organizacao (database_name);

create table if not exists usuario_global (
    id_usuario bigint primary key default nextval('seq_central_usuario'),
    nu_cpf varchar(11) not null,
    nm_usuario varchar(255),
    nm_email varchar(255),
    ds_senha varchar(255) not null,
    tp_global varchar(30) not null default 'DEFAULT',
    fl_ativo boolean not null default true,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now(),
    constraint ck_usuario_tp_global check (tp_global in ('SUPER_ADMIN', 'DEFAULT'))
);

create unique index if not exists ux_usuario_nu_cpf on usuario_global (nu_cpf);

create table if not exists usuario_organizacao (
    id_usuario_organizacao bigint primary key default nextval('seq_central_usuario_organizacao'),
    id_usuario bigint not null references usuario_global (id_usuario),
    id_organizacao bigint not null references organizacao (id_organizacao),
    ds_role varchar(30) not null,
    fl_ativo boolean not null default true,
    dt_criacao timestamp not null default now(),
    constraint ck_usuario_organizacao_role check (ds_role in ('ADMIN', 'USER'))
);

create unique index if not exists ux_usuario_organizacao_vinculo
    on usuario_organizacao (id_usuario, id_organizacao);

create table if not exists permissao_global (
    id_permissao bigint primary key default nextval('seq_central_permissao'),
    nm_permissao varchar(255) not null,
    nm_chave varchar(255) not null,
    fl_ativo boolean not null default true
);

create unique index if not exists ux_permissao_nm_chave on permissao_global (nm_chave);

create table if not exists papel (
    id_papel bigint primary key default nextval('seq_central_papel'),
    nm_papel varchar(80) not null,
    fl_ativo boolean not null default true
);

create unique index if not exists ux_papel_nm_papel on papel (nm_papel);

create table if not exists papel_permissao (
    id_papel_permissao bigint primary key default nextval('seq_central_papel_permissao'),
    id_papel bigint not null references papel (id_papel),
    id_organizacao bigint not null references organizacao (id_organizacao),
    id_permissao bigint not null references permissao_global (id_permissao)
);

create unique index if not exists ux_papel_permissao
    on papel_permissao (id_papel, id_organizacao, id_permissao);

create table if not exists usuario_permissao (
    id_usuario_permissao bigint primary key default nextval('seq_central_usuario_permissao'),
    id_usuario bigint not null references usuario_global (id_usuario),
    id_organizacao bigint not null references organizacao (id_organizacao),
    id_permissao bigint not null references permissao_global (id_permissao)
);

create unique index if not exists ux_usuario_permissao
    on usuario_permissao (id_usuario, id_organizacao, id_permissao);

create table if not exists provisionamento_tenant (
    id_provisionamento bigint primary key default nextval('seq_central_provisionamento'),
    id_organizacao bigint not null references organizacao (id_organizacao),
    status varchar(40) not null,
    etapa_atual varchar(80),
    database_name varchar(120) not null,
    tentativas integer not null default 0,
    erro text,
    dt_inicio timestamp not null default now(),
    dt_fim timestamp
);
