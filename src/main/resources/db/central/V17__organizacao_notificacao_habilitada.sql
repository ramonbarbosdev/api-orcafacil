alter table organizacao
    add column if not exists fl_notificacao_habilitada boolean not null default false;
