create table if not exists papel_permissao_padrao (
    id_papel bigint not null references papel (id_papel),
    id_permissao bigint not null references permissao_global (id_permissao),
    primary key (id_papel, id_permissao)
);

create table if not exists plano_permissao (
    id_planoassinatura bigint not null references plano_assinatura (id_planoassinatura),
    id_permissao bigint not null references permissao_global (id_permissao),
    primary key (id_planoassinatura, id_permissao)
);

-- ADMIN: todas as permissoes ativas
insert into papel_permissao_padrao (id_papel, id_permissao)
select 1, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
on conflict do nothing;

-- USER: leitura + orcamentos + perfil
insert into papel_permissao_padrao (id_papel, id_permissao)
select 2, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
  and (
    pg.nm_chave like '%.ler'
    or pg.nm_chave in ('orcamentos.criar', 'orcamentos.editar')
    or pg.nm_chave like 'perfil.%'
  )
on conflict do nothing;

-- Incorpora configuracoes ja existentes por organizacao (se houver)
insert into papel_permissao_padrao (id_papel, id_permissao)
select distinct pp.id_papel, pp.id_permissao
from papel_permissao pp
on conflict do nothing;

-- Plano gratuito: todos os recursos
insert into plano_permissao (id_planoassinatura, id_permissao)
select 1, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
on conflict do nothing;

update organizacao
set id_planoassinatura = 1
where id_planoassinatura is null;
