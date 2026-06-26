-- Garante assinatura TRIAL/ATIVA para organizações sem registro (dev e orgs antigas)
insert into organizacao_assinatura (id_organizacao, id_planoassinatura, tp_status, dt_inicio, fl_renovacao_automatica)
select o.id_organizacao,
       coalesce(o.id_planoassinatura, 1),
       case when o.status = 'ATIVA' then 'ATIVA' else 'TRIAL' end,
       coalesce(o.dt_criacao, now()),
       true
from organizacao o
where not exists (
    select 1
    from organizacao_assinatura oa
    where oa.id_organizacao = o.id_organizacao
      and oa.tp_status in ('TRIAL', 'ATIVA')
      and (oa.dt_fim is null or oa.dt_fim > now())
      and (
          oa.tp_status <> 'TRIAL'
          or oa.dt_fim_trial is null
          or oa.dt_fim_trial > now()
      )
);

-- Reativa trials expirados sem data de fim definida (ambiente local)
update organizacao_assinatura
set dt_fim_trial = now() + interval '30 days',
    dt_atualizacao = now()
where tp_status = 'TRIAL'
  and dt_fim_trial is not null
  and dt_fim_trial < now()
  and (dt_fim is null or dt_fim > now());
