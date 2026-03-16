create table predictions
(
    id           text                    not null
        constraint predictions_pk
            primary key,
    team         varchar(4),
    user_id      varchar(64) not null,
    match_number smallint,
    amount       smallint not null,
    status       varchar(20) default 'PLACED'::character varying not null,
    result_amt   float4 default 0 not null,
    is_double    bool default false not null,
    created_at      timestamp   default now()  not null,
    updated_at      timestamp   default now()  not null
);

alter table predictions
    add constraint predictions_team_fk
        foreign key (team) references teams(short_name),
    add constraint predictions_match_fk
        foreign key (match_number) references matches(number),
    add constraint predictions_user_fk
        foreign key (user_id) references users(clerk_id),
    add constraint predictions_unique_key
        unique (match_number,user_id),
    add constraint check_amount
        check(predictions.amount>0),
    add constraint check_team
        check (predictions.team in ('CSK', 'MI','RR','RCB','GT',
                              'SRH','KKR','LSG','PBKS','DC'));