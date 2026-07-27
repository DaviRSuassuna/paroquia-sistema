package com.paroquia.repository;

import com.paroquia.model.ConfiguracaoSistema;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ConfiguracaoSistemaRepository implements PanacheRepository<ConfiguracaoSistema> {

    /**
     * Entidade singleton — espera-se exatamente um registro na tabela.
     */
    public Optional<ConfiguracaoSistema> getConfiguracao() {
        return findAll().firstResultOptional();
    }
}
