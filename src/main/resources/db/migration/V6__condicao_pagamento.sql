create sequence if not exists seq_codicao_pagamento start with 100 increment by 1;

create table if not exists codicao_pagamento (
    id_codicaopagamento bigint primary key default nextval('seq_codicao_pagamento'),
    id_organizacao bigint not null,
    cd_codicaopagamento varchar(50) not null,
    nm_codicaopagamento varchar(255) not null,
    dt_criacao timestamp not null default now(),
    dt_atualizacao timestamp not null default now()
);

create index if not exists ix_condicao_pagamento_org on codicao_pagamento (id_organizacao);
