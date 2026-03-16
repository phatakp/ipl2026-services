create table teams
(
    short_name    varchar(4)             not null
        constraint teams_pk
            primary key,
    full_name     varchar(100)           not null,
    played        smallint default 0     not null,
    won           smallint default 0     not null,
    lost          smallint default 0     not null,
    draw          smallint default 0     not null,
    no_result     smallint default 0     not null,
    points        smallint default 0     not null,
    nrr           float4   default 0.000 not null,
    for_runs      integer  default 0     not null,
    for_balls     integer  default 0     not null,
    against_runs  integer  default 0     not null,
    against_balls integer  default 0     not null
);

alter table teams
    add constraint check_short_name
        check (teams.short_name in ('CSK', 'MI','RR','RCB','GT',
                                   'SRH','KKR','LSG','PBKS','DC'));