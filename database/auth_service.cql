create keyspace if not exists auth_service with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create user auth_service with password 'auth_service' nosuperuser;
grant all permissions on keyspace auth_service to auth_service;
create table if not exists auth_service.client_details
(
    client text,
    secret text,
    roles set<text>,
    primary key (client)
) with comment = 'Пользователи';