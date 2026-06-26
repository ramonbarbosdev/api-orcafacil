-- Novos modulos comerciais
insert into permissao_global (id_permissao, nm_permissao, nm_chave, fl_ativo) values
    (49, 'Exibir dashboard', 'dashboard.exibir', true),
    (50, 'Acessar dashboard', 'dashboard.ler', true),
    (51, 'Exibir relatorios', 'relatorios.exibir', true),
    (52, 'Acessar relatorios', 'relatorios.ler', true),
    (53, 'Gerar PDF orcamento', 'orcamentos.pdf', true),
    (54, 'Link publico orcamento', 'orcamentos.link-publico', true),
    (55, 'Exibir API', 'api.exibir', true),
    (56, 'Acessar API', 'api.ler', true),
    (57, 'Configurar API', 'api.criar', true),
    (58, 'Exibir webhooks', 'webhooks.exibir', true),
    (59, 'Configurar webhooks', 'webhooks.criar', true),
    (60, 'Exibir integracoes', 'integracoes.exibir', true),
    (61, 'Usar integracoes', 'integracoes.ler', true),
    (62, 'Configurar integracoes', 'integracoes.criar', true),
    (63, 'Exibir WhatsApp', 'whatsapp.exibir', true),
    (64, 'Usar WhatsApp', 'whatsapp.ler', true),
    (65, 'Configurar WhatsApp', 'whatsapp.criar', true),
    (66, 'Backup', 'backup.ler', true),
    (67, 'Restaurar backup', 'backup.criar', true)
on conflict (nm_chave) do nothing;

insert into plano_assinatura (id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens, nu_limiteatendentes, fl_ativo)
values
    (2, 'Starter', 49.90, 500, 3, true),
    (3, 'Professional', 99.90, 2000, 10, true),
    (4, 'Enterprise', 199.90, 10000, 50, true)
on conflict do nothing;

-- Starter: clientes, servicos, orcamentos basicos, PDF, link, config basica
insert into plano_permissao (id_planoassinatura, id_permissao)
select 2, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
  and pg.nm_chave in (
    'clientes.exibir', 'clientes.ler', 'clientes.criar', 'clientes.editar', 'clientes.deletar',
    'servicos.exibir', 'servicos.ler', 'servicos.criar', 'servicos.editar', 'servicos.deletar',
    'categorias-servico.exibir', 'categorias-servico.ler', 'categorias-servico.criar', 'categorias-servico.editar',
    'orcamentos.exibir', 'orcamentos.ler', 'orcamentos.criar', 'orcamentos.editar', 'orcamentos.deletar',
    'orcamentos.pdf', 'orcamentos.link-publico',
    'condicoes-pagamento.exibir', 'condicoes-pagamento.ler',
    'configuracao-orcamento.exibir', 'configuracao-orcamento.ler', 'configuracao-orcamento.editar',
    'perfil.ler', 'perfil.editar', 'perfil.criar', 'perfil.deletar'
  )
on conflict do nothing;

-- Professional: Starter + dashboard, relatorios, precificacao, campos, catalogo, equipe
insert into plano_permissao (id_planoassinatura, id_permissao)
select 3, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
  and (
    pg.nm_chave in (select nm_chave from permissao_global pp join plano_permissao pl on pl.id_permissao = pp.id_permissao where pl.id_planoassinatura = 2)
    or pg.nm_chave like 'catalogos.%'
    or pg.nm_chave like 'campos-personalizados.%'
    or pg.nm_chave like 'metodos-precificacao.%'
    or pg.nm_chave like 'metodos-ajuste.%'
    or pg.nm_chave like 'empresa-metodos-precificacao.%'
    or pg.nm_chave like 'dashboard.%'
    or pg.nm_chave like 'relatorios.%'
    or pg.nm_chave like 'condicoes-pagamento.criar'
    or pg.nm_chave like 'condicoes-pagamento.editar'
    or pg.nm_chave like 'condicoes-pagamento.deletar'
  )
on conflict do nothing;

-- Enterprise: Professional + API, webhooks, integracoes, whatsapp, backup
insert into plano_permissao (id_planoassinatura, id_permissao)
select 4, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
  and (
    pg.nm_chave in (select nm_chave from permissao_global pp join plano_permissao pl on pl.id_permissao = pp.id_permissao where pl.id_planoassinatura = 3)
    or pg.nm_chave like 'api.%'
    or pg.nm_chave like 'webhooks.%'
    or pg.nm_chave like 'integracoes.%'
    or pg.nm_chave like 'whatsapp.%'
    or pg.nm_chave like 'backup.%'
  )
on conflict do nothing;

-- Limites por plano
insert into plano_limite (id_planoassinatura, nm_chave_limite, nu_valor) values
    (2, 'usuarios', 3),
    (2, 'clientes', 100),
    (2, 'servicos', 50),
    (2, 'orcamentos_mes', 50),
    (2, 'mensagens', 500),
    (3, 'usuarios', 10),
    (3, 'clientes', 500),
    (3, 'servicos', 200),
    (3, 'orcamentos_mes', 200),
    (3, 'mensagens', 2000),
    (4, 'usuarios', 50),
    (4, 'clientes', null),
    (4, 'servicos', null),
    (4, 'orcamentos_mes', null),
    (4, 'mensagens', 10000),
    (4, 'armazenamento_mb', null)
on conflict do nothing;

-- Plano gratuito recebe novas permissoes comerciais (compatibilidade MVP)
insert into plano_permissao (id_planoassinatura, id_permissao)
select 1, pg.id_permissao
from permissao_global pg
where pg.fl_ativo = true
  and pg.id_permissao >= 49
on conflict do nothing;
