alter table organizacao
    add column if not exists ds_api_key_notificacao varchar(512);
