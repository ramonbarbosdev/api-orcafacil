create sequence if not exists seq_metodoprecificacao start with 100 increment by 1;

create table if not exists metodo_precificacao (
    id_metodoprecificacao bigint primary key default nextval('seq_metodoprecificacao'),
    cd_metodoprecificacao varchar(30) not null,
    nm_metodoprecificacao varchar(255) not null,
    ds_metodoprecificacao text,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

insert into metodo_precificacao (id_metodoprecificacao, cd_metodoprecificacao, nm_metodoprecificacao, ds_metodoprecificacao) values
    (1, 'MARKUP', 'Markup', 'Aplica percentual sobre o custo'),
    (2, 'MARGEM', 'Margem', 'Calcula preco pela margem desejada'),
    (3, 'FIXO', 'Valor fixo', 'Adiciona valor fixo ao custo'),
    (4, 'SIMPLES', 'Simples', 'Repasse direto do custo')
on conflict do nothing;
