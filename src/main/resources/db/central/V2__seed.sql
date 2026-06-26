-- Senha: admin123
insert into usuario_global (
    id_usuario, nu_cpf, nm_usuario, nm_email, ds_senha, tp_global, fl_ativo
) values (
    1, '52998224725', 'Super Admin', 'admin@local.dev',
    '$2a$10$MUE55iT4HfIh3PPjMQgKNeeGAvQe7OE2DMJxLA76EGC3l2ALzm0Cy',
    'SUPER_ADMIN', true
) on conflict (nu_cpf) do nothing;

insert into papel (id_papel, nm_papel, fl_ativo) values
    (1, 'ADMIN', true),
    (2, 'USER', true)
on conflict (nm_papel) do nothing;

insert into permissao_global (id_permissao, nm_permissao, nm_chave, fl_ativo) values
    (1, 'Listar clientes', 'clientes.ler', true),
    (2, 'Criar clientes', 'clientes.criar', true),
    (3, 'Editar clientes', 'clientes.editar', true),
    (4, 'Deletar clientes', 'clientes.deletar', true),
    (5, 'Listar catalogos', 'catalogos.ler', true),
    (6, 'Criar catalogos', 'catalogos.criar', true),
    (7, 'Editar catalogos', 'catalogos.editar', true),
    (8, 'Deletar catalogos', 'catalogos.deletar', true),
    (9, 'Listar servicos', 'servicos.ler', true),
    (10, 'Criar servicos', 'servicos.criar', true),
    (11, 'Editar servicos', 'servicos.editar', true),
    (12, 'Deletar servicos', 'servicos.deletar', true),
    (13, 'Listar categorias de servico', 'categorias-servico.ler', true),
    (14, 'Criar categorias de servico', 'categorias-servico.criar', true),
    (15, 'Editar categorias de servico', 'categorias-servico.editar', true),
    (16, 'Deletar categorias de servico', 'categorias-servico.deletar', true),
    (17, 'Listar orcamentos', 'orcamentos.ler', true),
    (18, 'Criar orcamentos', 'orcamentos.criar', true),
    (19, 'Editar orcamentos', 'orcamentos.editar', true),
    (20, 'Deletar orcamentos', 'orcamentos.deletar', true),
    (21, 'Listar condicoes de pagamento', 'condicoes-pagamento.ler', true),
    (22, 'Criar condicoes de pagamento', 'condicoes-pagamento.criar', true),
    (23, 'Editar condicoes de pagamento', 'condicoes-pagamento.editar', true),
    (24, 'Deletar condicoes de pagamento', 'condicoes-pagamento.deletar', true),
    (25, 'Listar configuracao de orcamento', 'configuracao-orcamento.ler', true),
    (26, 'Criar configuracao de orcamento', 'configuracao-orcamento.criar', true),
    (27, 'Editar configuracao de orcamento', 'configuracao-orcamento.editar', true),
    (28, 'Deletar configuracao de orcamento', 'configuracao-orcamento.deletar', true),
    (29, 'Listar metodos de precificacao', 'metodos-precificacao.ler', true),
    (30, 'Criar metodos de precificacao', 'metodos-precificacao.criar', true),
    (31, 'Editar metodos de precificacao', 'metodos-precificacao.editar', true),
    (32, 'Deletar metodos de precificacao', 'metodos-precificacao.deletar', true),
    (33, 'Listar campos personalizados', 'campos-personalizados.ler', true),
    (34, 'Criar campos personalizados', 'campos-personalizados.criar', true),
    (35, 'Editar campos personalizados', 'campos-personalizados.editar', true),
    (36, 'Deletar campos personalizados', 'campos-personalizados.deletar', true),
    (37, 'Listar metodos de ajuste', 'metodos-ajuste.ler', true),
    (38, 'Criar metodos de ajuste', 'metodos-ajuste.criar', true),
    (39, 'Editar metodos de ajuste', 'metodos-ajuste.editar', true),
    (40, 'Deletar metodos de ajuste', 'metodos-ajuste.deletar', true),
    (41, 'Listar metodos de precificacao da empresa', 'empresa-metodos-precificacao.ler', true),
    (42, 'Criar metodos de precificacao da empresa', 'empresa-metodos-precificacao.criar', true),
    (43, 'Editar metodos de precificacao da empresa', 'empresa-metodos-precificacao.editar', true),
    (44, 'Deletar metodos de precificacao da empresa', 'empresa-metodos-precificacao.deletar', true),
    (45, 'Ver perfil', 'perfil.ler', true),
    (46, 'Editar perfil', 'perfil.editar', true),
    (47, 'Upload foto perfil', 'perfil.criar', true),
    (48, 'Remover foto perfil', 'perfil.deletar', true)
on conflict (nm_chave) do nothing;
