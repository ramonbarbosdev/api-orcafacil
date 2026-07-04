create unique index if not exists ux_cliente_cpfcnpj_org on cliente (id_organizacao, nu_cpfcnpj);
create unique index if not exists ux_catalogo_cd_org on catalogo (id_organizacao, cd_catalogo);
create unique index if not exists ux_servico_cd_org on servico (id_organizacao, cd_servico);
create unique index if not exists ux_categoria_servico_cd_org on categoria_servico (id_organizacao, cd_categoriaservico);
create unique index if not exists ux_condicao_pagamento_cd_org on codicao_pagamento (id_organizacao, cd_codicaopagamento);
create unique index if not exists ux_campos_personalizados_cd_org on campos_personalizados (id_organizacao, cd_campopersonalizado);
create unique index if not exists ux_orcamento_nu_org on orcamento (id_organizacao, nu_orcamento);
create unique index if not exists ux_metodo_precificacao_cd on metodo_precificacao (cd_metodoprecificacao);
