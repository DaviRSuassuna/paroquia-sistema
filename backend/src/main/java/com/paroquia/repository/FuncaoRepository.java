package com.paroquia.repository;

import com.paroquia.model.Funcao;
import com.paroquia.model.Pastoral;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FuncaoRepository implements PanacheRepository<Funcao> {

    public Optional<Funcao> findByNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }

    public List<Funcao> findAtivas() {
        return list("ativo", true);
    }

    public List<Funcao> findByPastoral(Pastoral pastoral) {
        return list("pastoral", pastoral);
    }
}
