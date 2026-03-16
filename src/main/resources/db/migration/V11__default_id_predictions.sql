alter table predictions
    alter column id set default gen_random_uuid();