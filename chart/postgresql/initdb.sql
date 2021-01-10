create table if not exists bill (client_id varchar(250) primary key, balance bigint, update_time timestamp default now());
create table if not exists bill_increase (id serial, client_id varchar(250), amount bigint, ins_time timestamp default now(), constraint bill_increase_pk primary key (id));
create table if not exists bill_decrease (id serial, client_id varchar(250), amount bigint, order_id varchar(250), ins_time timestamp default now(), constraint bill_decrease_pk primary key (id));
create index if not exists bill_decrease_order_id_idx on bill_decrease (order_id);
