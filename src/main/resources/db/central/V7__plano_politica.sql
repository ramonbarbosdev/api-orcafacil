create table if not exists tipo_limite (
    id_tipolimite bigint primary key generated always as identity,
    nm_chave varchar(80) not null unique,
    nm_limite varchar(255) not null,
    ds_limite text,
    tp_limite varchar(30) not null default 'CONTAGEM',
    fl_ativo boolean not null default true
);

create table if not exists plano_limite (
    id_planoassinatura bigint not null references plano_assinatura (id_planoassinatura),
    nm_chave_limite varchar(80) not null references tipo_limite (nm_chave),
    nu_valor bigint,
    primary key (id_planoassinatura, nm_chave_limite)
);

create table if not exists organizacao_assinatura (
    id_organizacaoassinatura bigint primary key generated always as identity,
    id_organizacao bigint not null references organizacao (id_organizacao),
    id_planoassinatura bigint not null references plano_assinatura (id_planoassinatura),
    tp_status varchar(30) not null default 'ATIVA',
    dt_inicio timestamp not null default now(),
    dt_fim timestamp,
    dt_fim_trial timestamp,
    dt_proximo_ciclo timestamp,
    fl_renovacao_automatica boolean not null default true,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create unique index if not exists uk_organizacao_assinatura_ativa
    on organizacao_assinatura (id_organizacao)
    where tp_status in ('TRIAL', 'ATIVA');

create table if not exists organizacao_consumo (
    id_organizacao bigint not null references organizacao (id_organizacao),
    nm_chave_limite varchar(80) not null references tipo_limite (nm_chave),
    dt_referencia date not null default current_date,
    nu_consumo bigint not null default 0,
    primary key (id_organizacao, nm_chave_limite, dt_referencia)
);

insert into tipo_limite (nm_chave, nm_limite, ds_limite, tp_limite) values
    ('usuarios', 'Usuarios', 'Quantidade maxima de usuarios vinculados', 'CONTAGEM'),
    ('clientes', 'Clientes', 'Quantidade maxima de clientes cadastrados', 'CONTAGEM'),
    ('servicos', 'Servicos', 'Quantidade maxima de servicos cadastrados', 'CONTAGEM'),
    ('orcamentos_mes', 'Orcamentos por mes', 'Quantidade maxima de orcamentos criados no mes', 'PERIODO'),
    ('mensagens', 'Mensagens', 'Quantidade maxima de mensagens', 'CONTAGEM'),
    ('armazenamento_mb', 'Armazenamento (MB)', 'Espaco maximo de armazenamento em MB', 'ARMAZENAMENTO')
on conflict (nm_chave) do nothing;

insert into plano_limite (id_planoassinatura, nm_chave_limite, nu_valor)
select 1, 'mensagens', nu_limitemensagens from plano_assinatura where id_planoassinatura = 1
on conflict do nothing;

insert into plano_limite (id_planoassinatura, nm_chave_limite, nu_valor)
select 1, 'usuarios', nu_limiteatendentes from plano_assinatura where id_planoassinatura = 1
on conflict do nothing;

insert into plano_limite (id_planoassinatura, nm_chave_limite, nu_valor)
values
    (1, 'clientes', 50),
    (1, 'servicos', 20),
    (1, 'orcamentos_mes', 30)
on conflict do nothing;

insert into organizacao_assinatura (id_organizacao, id_planoassinatura, tp_status, dt_inicio, fl_renovacao_automatica)
select o.id_organizacao,
       coalesce(o.id_planoassinatura, 1),
       case when o.status = 'ATIVA' then 'ATIVA' else 'TRIAL' end,
       coalesce(o.dt_criacao, now()),
       true
from organizacao o
where not exists (
    select 1 from organizacao_assinatura oa
    where oa.id_organizacao = o.id_organizacao
      and oa.tp_status in ('TRIAL', 'ATIVA')
);
