create keyspace if not exists auth_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user auth_service with password 'auth_service' nosuperuser;
grant all permissions on keyspace auth_service to auth_service;
create table if not exists auth_service.client_details(client text, secret text, roles set<text>, primary key (client));

create keyspace if not exists user_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user user_service with password 'user_service' nosuperuser;
grant all permissions on keyspace user_service to user_service;
create table if not exists user_service.user(client text, fullname text, primary key (client));

create keyspace if not exists order_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user order_service with password 'order_service' nosuperuser;
grant all permissions on keyspace order_service to order_service;
create table if not exists order_service.order_details(id UUID, client_id text, amount bigint, ins_time timestamp, primary key (id));
create table if not exists order_service.order_status(id UUID, ins_time timestamp, status text, primary key (id, ins_time)) with clustering order by (ins_time desc);
