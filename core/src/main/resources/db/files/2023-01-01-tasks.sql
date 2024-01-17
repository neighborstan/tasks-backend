--liquibase formatted sql
--changeset romeme:2023-01-01-tasks

create table if not exists tasks
(
    id         uuid        not null primary key,
    pipeline   text        not null,
    type       text        not null,
    status     text        not null,
    author     text        not null,
    story      jsonb       not null,
    transition timestamptz not null,
    contacts   jsonb       not null,
    entity     jsonb       not null,
    payload    jsonb       not null,

    comment    text        not null,

    unique (id, pipeline)
);

create table if not exists assignment
(
    task     uuid        not null,
    pipeline text        not null,
    actor    text        not null,
    instant  timestamptz not null,

    primary key (task, actor),
    foreign key (task, pipeline) references tasks (id, pipeline)

);
