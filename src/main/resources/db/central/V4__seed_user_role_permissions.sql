-- Permissões padrão do papel USER para organizações já provisionadas (ADMIN já recebe tudo no provisionamento).
insert into papel_permissao (id_papel, id_organizacao, id_permissao)
select 2, o.id_organizacao, pg.id_permissao
from organizacao o
cross join permissao_global pg
where o.fl_ativo = true
  and o.status = 'ATIVA'
  and pg.fl_ativo = true
  and (
    pg.nm_chave like '%.ler'
    or pg.nm_chave in ('orcamentos.criar', 'orcamentos.editar')
    or pg.nm_chave like 'perfil.%'
  )
on conflict do nothing;
