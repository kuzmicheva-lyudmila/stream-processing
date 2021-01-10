create keyspace if not exists order_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user order_service with password 'order_service' nosuperuser;
grant all permissions on keyspace order_service to order_service;

create table if not exists order_service.order_details
(
    id UUID,
    client_id text,
    amount bigint,
    ins_time timestamp,
    primary key (id)
);

create table if not exists order_service.order_status
(
    id UUID,
    ins_time timestamp,
    status text,
    primary key (id, ins_time)
) with clustering order by (ins_time desc);
