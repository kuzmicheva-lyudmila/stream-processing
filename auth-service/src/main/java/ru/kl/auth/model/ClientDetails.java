package ru.kl.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;

@Table("client_details")
@Data
@AllArgsConstructor
public class ClientDetails {

    @PrimaryKey("client")
    private String client;

    private String secret;

    private Set<Role> roles;
}
