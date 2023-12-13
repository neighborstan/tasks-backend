--liquibase formatted sql
--changeset durov:2023-01-01-tasks

drop table assignment;
drop table tasks;

create table if not exists tasks
(
    id         uuid        not null primary key,
    pipeline   text        not null,
    author     text        not null,
    status     text        not null,
    story      jsonb       not null,
    transition timestamptz not null,
    entity     jsonb       not null,
    payload    jsonb       not null,

    comment    text        not null
);

create table if not exists assignment
(
    task    uuid        not null references tasks (id),
    actor   text        not null,
    instant timestamptz not null,
    primary key (task, actor)
);
