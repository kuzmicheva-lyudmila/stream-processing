create keyspace if not exists user_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user user_service with password 'user_service' nosuperuser;
grant all permissions on keyspace user_service to user_service;
create table if not exists user_service.user
(
    client text,
    fullname text,
    primary key (client)
) with comment = 'Пользователи';