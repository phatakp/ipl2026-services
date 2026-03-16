alter table matches
    add max_double_amt smallint default 50 not null,
    add is_updated bool default false not null;