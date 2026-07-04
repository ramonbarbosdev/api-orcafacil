create sequence if not exists seq_orcamento_notificacao start with 100 increment by 1;

create table if not exists orcamento_notificacao (
    id_orcamento_notificacao bigint primary key default nextval('seq_orcamento_notificacao'),
    id_organizacao bigint not null,
    id_orcamento bigint not null references orcamento (id_orcamento) on delete cascade,
    tp_canal varchar(30) not null,
    ds_destinatario varchar(255),
    fl_sucesso boolean not null default false,
    id_notificacao_externa bigint,
    ds_erro text,
    ds_mensagem text,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_orcamento_notificacao_orcamento
    on orcamento_notificacao (id_orcamento, dt_criacao desc);
