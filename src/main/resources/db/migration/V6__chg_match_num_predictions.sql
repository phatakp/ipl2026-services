alter table predictions
    alter column match_number type integer using match_number::integer;