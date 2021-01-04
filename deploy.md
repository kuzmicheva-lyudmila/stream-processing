-- namespace=myapp

helm install auth-service chart/auth-service/

-- подождать статусы Running для cassandra и auth-service

helm install user-service chart/user-service/
