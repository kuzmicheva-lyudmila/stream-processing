package ru.kl.users.repository;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import ru.kl.users.model.User;

public interface UserRepository extends ReactiveCassandraRepository<User, String> {
}
