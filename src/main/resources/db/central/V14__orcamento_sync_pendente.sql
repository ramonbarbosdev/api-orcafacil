create sequence if not exists seq_orcamento_sync_pendente start with 1 increment by 1;

create table if not exists orcamento_sync_pendente (
    id_orcamento_sync_pendente bigint primary key default nextval('seq_orcamento_sync_pendente'),
    id_organizacao bigint not null,
    id_orcamento bigint,
    cd_publico varchar(64),
    tp_operacao varchar(20) not null,
    nu_tentativas integer not null default 0,
    fl_novo boolean not null default true,
    ds_erro text,
    dt_proximo_retry timestamp not null,
    dt_criacao timestamp not null default now()
);

create index if not exists ix_orcamento_sync_pendente_retry
    on orcamento_sync_pendente (dt_proximo_retry)
    where nu_tentativas < 5;
