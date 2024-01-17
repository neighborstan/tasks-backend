--liquibase formatted sql
--changeset romeme:2024-01-10-identity


create table if not exists identity
(
    actor text not null,
    id    uuid not null,

    primary key (actor, id)
);

