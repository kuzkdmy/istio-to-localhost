CREATE TABLE destinations
(
    id   varchar(50) primary key,
    name text not null
);

CREATE TABLE integrations
(
    id             varchar(50) primary key,
    destination_id varchar(50) not null,
    name           text        not null,
    FOREIGN KEY (destination_id) REFERENCES destinations (id)
);
