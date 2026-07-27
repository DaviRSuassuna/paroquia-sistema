package com.paroquia.repository;

import com.paroquia.model.Pastoral;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PastoralRepository implements PanacheRepository<Pastoral> {

    public Optional<Pastoral> findByNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }

    public List<Pastoral> findAtivas() {
        return list("ativo", true);
    }
}
