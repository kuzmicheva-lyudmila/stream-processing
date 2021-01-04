package ru.kl.auth.repository;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import ru.kl.auth.model.ClientDetails;

public interface ClientRepository extends ReactiveCassandraRepository<ClientDetails, String> {
}
