create table matches
(
    number      integer                not null
        constraint matches_pk
            primary key,
    home_team     varchar(4)            not null,
    away_team     varchar(4)            not null,
    winner        varchar(4),
    date          date                  not null,
    time          varchar(10)           not null,
    venue         varchar(40)           not null,
    status        varchar(20) default 'SCHEDULED'::character varying not null,
    type          varchar(20) default 'LEAGUE'::character varying not null,
    result_type   varchar(20),
    result_margin  smallint default 0,
    min_stake     smallint  default 30  not null,
    is_double     boolean   default false not null,
    home_score      text,
    away_score      text
);

alter table matches
    add constraint matches_home_team_fk
        foreign key (home_team) references teams(short_name),
    add constraint matches_away_team_fk
        foreign key (away_team) references teams(short_name),
    add constraint matches_winner_fk
        foreign key (winner) references teams(short_name),
    add constraint check_status
        check (matches.status in ('SCHEDULED', 'COMPLETED','ABANDONED')),
    add constraint check_type
        check (matches.type in ('LEAGUE','QUALIFIER1','QUALIFIER2','ELIMINATOR','FINAL')),
    add constraint check_result_type
        check (matches.result_type in ('RUNS','WICKETS','SUPEROVER',null));