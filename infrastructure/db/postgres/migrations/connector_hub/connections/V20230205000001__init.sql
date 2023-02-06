CREATE TABLE connections
(
    id             varchar(50) primary key,
    destination_id varchar(50) not null,
    integration_id varchar(50) not null,
    name           varchar(50) not null,
    created_at     timestamp   not null,
    updated_at     timestamp   not null
);
