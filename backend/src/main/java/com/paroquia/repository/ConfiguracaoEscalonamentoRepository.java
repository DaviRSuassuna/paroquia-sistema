package com.paroquia.repository;

import com.paroquia.model.ConfiguracaoEscalonamento;
import com.paroquia.model.Pastoral;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ConfiguracaoEscalonamentoRepository implements PanacheRepository<ConfiguracaoEscalonamento> {

    public Optional<ConfiguracaoEscalonamento> findByPastoral(Pastoral pastoral) {
        return find("pastoral", pastoral).firstResultOptional();
    }
}
