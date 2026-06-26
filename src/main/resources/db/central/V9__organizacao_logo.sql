create sequence if not exists seq_central_organizacao_logo start with 100 increment by 1;

create table if not exists organizacao_logo (
    id_organizacao_logo bigint primary key default nextval('seq_central_organizacao_logo'),
    id_organizacao bigint not null references organizacao (id_organizacao),
    ds_caminho_interno varchar(500) not null,
    nm_arquivo_original varchar(255),
    nm_arquivo_salvo varchar(255) not null,
    ds_content_type varchar(100) not null,
    ds_extensao varchar(10) not null,
    nu_tamanho_bytes bigint not null,
    nu_largura integer not null,
    nu_altura integer not null,
    id_usuario_upload bigint references usuario_global (id_usuario),
    fl_ativo boolean not null default true,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now(),
    constraint ux_organizacao_logo_org unique (id_organizacao)
);

create index if not exists ix_organizacao_logo_org on organizacao_logo (id_organizacao);

insert into permissao_global (id_permissao, nm_permissao, nm_chave, fl_ativo) values
    (68, 'Exibir organizacao no menu', 'organizacao.exibir', true),
    (69, 'Consultar logo da organizacao', 'organizacao.ler', true),
    (70, 'Enviar logo da organizacao', 'organizacao.criar', true),
    (71, 'Atualizar logo da organizacao', 'organizacao.editar', true),
    (72, 'Remover logo da organizacao', 'organizacao.deletar', true)
on conflict (nm_chave) do nothing;

-- ADMIN: todas as permissoes de organizacao
insert into papel_permissao_padrao (id_papel, id_permissao)
select 1, pg.id_permissao
from permissao_global pg
where pg.nm_chave like 'organizacao.%'
  and pg.fl_ativo = true
on conflict do nothing;

-- USER: apenas consultar logo
insert into papel_permissao_padrao (id_papel, id_permissao)
select 2, pg.id_permissao
from permissao_global pg
where pg.nm_chave in ('organizacao.ler', 'organizacao.exibir')
  and pg.fl_ativo = true
on conflict do nothing;

-- Organizacoes existentes
insert into papel_permissao (id_papel, id_organizacao, id_permissao)
select 1, o.id_organizacao, pg.id_permissao
from organizacao o
cross join permissao_global pg
where o.fl_ativo = true
  and o.status = 'ATIVA'
  and pg.nm_chave like 'organizacao.%'
  and pg.fl_ativo = true
on conflict do nothing;

insert into papel_permissao (id_papel, id_organizacao, id_permissao)
select 2, o.id_organizacao, pg.id_permissao
from organizacao o
cross join permissao_global pg
where o.fl_ativo = true
  and o.status = 'ATIVA'
  and pg.nm_chave in ('organizacao.ler', 'organizacao.exibir')
  and pg.fl_ativo = true
on conflict do nothing;

-- Professional e Enterprise: logo personalizada
insert into plano_permissao (id_planoassinatura, id_permissao)
select pl.id_planoassinatura, pg.id_permissao
from (values (3), (4)) as pl(id_planoassinatura)
cross join permissao_global pg
where pg.nm_chave like 'organizacao.%'
  and pg.fl_ativo = true
on conflict do nothing;
