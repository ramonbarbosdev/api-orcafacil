-- Permissão de exibição no menu (independente de ler/criar/editar/deletar)
insert into permissao_global (nm_permissao, nm_chave, fl_ativo)
select
    'Exibir ' || trim(substring(pg.nm_permissao from 8)) || ' no menu',
    replace(pg.nm_chave, '.ler', '.exibir'),
    pg.fl_ativo
from permissao_global pg
where pg.nm_chave like '%.ler'
  and not exists (
      select 1
      from permissao_global existente
      where existente.nm_chave = replace(pg.nm_chave, '.ler', '.exibir')
  );

-- ADMIN: inclui exibir
insert into papel_permissao_padrao (id_papel, id_permissao)
select 1, pg.id_permissao
from permissao_global pg
where pg.nm_chave like '%.exibir'
  and pg.fl_ativo = true
on conflict do nothing;

-- USER: exibir nos módulos em que já pode listar
insert into papel_permissao_padrao (id_papel, id_permissao)
select 2, pg_exibir.id_permissao
from permissao_global pg_exibir
where pg_exibir.nm_chave like '%.exibir'
  and exists (
      select 1
      from papel_permissao_padrao pp
      join permissao_global pg_ler on pg_ler.id_permissao = pp.id_permissao
      where pp.id_papel = 2
        and pg_ler.nm_chave = replace(pg_exibir.nm_chave, '.exibir', '.ler')
  )
on conflict do nothing;

-- Planos existentes: mantém menu visível onde já havia leitura
insert into plano_permissao (id_planoassinatura, id_permissao)
select distinct pp.id_planoassinatura, pg_exibir.id_permissao
from plano_permissao pp
join permissao_global pg_ler on pg_ler.id_permissao = pp.id_permissao
join permissao_global pg_exibir on pg_exibir.nm_chave = replace(pg_ler.nm_chave, '.ler', '.exibir')
where pg_ler.nm_chave like '%.ler'
on conflict do nothing;
