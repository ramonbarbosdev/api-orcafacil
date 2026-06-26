create sequence if not exists seq_planoassinatura start with 100 increment by 1;

create table if not exists plano_assinatura (
    id_planoassinatura bigint primary key default nextval('seq_planoassinatura'),
    nm_planoassinatura varchar(255) not null,
    vl_mensal numeric(12, 2),
    nu_limitemensagens integer not null default 0,
    nu_limiteatendentes integer not null default 0,
    fl_ativo boolean not null default true,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

alter table organizacao
    add column if not exists id_planoassinatura bigint references plano_assinatura (id_planoassinatura),
    add column if not exists ds_email varchar(255),
    add column if not exists nu_telefone varchar(30),
    add column if not exists ds_webhook_url varchar(500);

alter table usuario_global
    add column if not exists ds_foto_url varchar(500);

insert into plano_assinatura (id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens, nu_limiteatendentes)
values (1, 'Gratuito', 0, 100, 2)
on conflict do nothing;
