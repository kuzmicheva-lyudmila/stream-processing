-- namespace=myapp

**KAFKA:**
```
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install kafka bitnami/kafka

kubectl run kafka-client --image docker.io/bitnami/kafka:2.7.0-debian-10-r1 --namespace myapp --command -- sleep infinity
kubectl exec --tty -i kafka-client --namespace myapp -- bash

-- create topics
kafka-topics.sh --create --topic users --if-not-exists --bootstrap-server kafka.myapp.svc.cluster.local:9092
kafka-topics.sh --create --topic orders --if-not-exists --bootstrap-server kafka.myapp.svc.cluster.local:9092
kafka-topics.sh --create --topic bills --if-not-exists --bootstrap-server kafka.myapp.svc.cluster.local:9092
```

**CASSANDRA:**
```
helm install cassandra -f chart/cassandra/values.yaml bitnami/cassandra
kubectl run cassandra-client --image docker.io/bitnami/cassandra:3.11.9-debian-10-r52 --namespace myapp
kubectl exec --tty -i cassandra-client -- bash
    cqlsh -u cassandra -p cassandra cassandra
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
```

**POSTGRESQL**
```
helm install postgresql -f chart/postgresql/values.yaml bitnami/postgresql
kubectl run postgresql-client --rm --tty -i --namespace myapp --image docker.io/bitnami/postgresql:11.10.0-debian-10-r52 --env="PGPASSWORD=postgres" --command -- psql --host postgresql -U postgr
es -d postgres -p 5432
    create table if not exists bill (client_id varchar(250) primary key, balance bigint, update_time timestamp default now());
    create table if not exists bill_increase (id serial, client_id varchar(250), amount bigint, ins_time timestamp default now(), constraint bill_increase_pk primary key (id));
    create table if not exists bill_decrease (id serial, client_id varchar(250), amount bigint, order_id varchar(250), ins_time timestamp default now(), constraint bill_decrease_pk primary key (id));
    create index if not exists bill_decrease_order_id_idx on bill_decrease (order_id);
```

**APPLICATIONS:**
```
helm install auth-service chart/auth-service/
helm install user-service chart/user-service/
helm install billing-service chart/billing-service/
helm install order-service chart/order-service/
helm install notification-service chart/notification-service/
```
