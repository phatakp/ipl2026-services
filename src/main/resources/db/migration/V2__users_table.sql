create table users
(
    clerk_id      varchar(64)             not null
        constraint users_pk
            primary key,
    email         varchar(100)           not null
        constraint users_pk_2
            unique,
    first_name    varchar(100)           not null,
    last_name     varchar(100)           not null,
    image_url     text,
    role          varchar(10) default 'PLAYER'::character varying not null,
    team          varchar(4)            not null,
    balance       float4   default 0.000 not null,
    doubles_left  smallint default 5     not null,
    created_at      timestamp   default now()                     not null,
    updated_at      timestamp   default now()                     not null
);

alter table users
    add constraint users_team_fk
        foreign key (team) references teams(short_name),
    add constraint check_users_role
        check (users.role in ('PLAYER', 'ADMIN')),
    add constraint check_team
        check (users.team in ('CSK', 'MI','RR','RCB','GT',
                              'SRH','KKR','LSG','PBKS','DC'));