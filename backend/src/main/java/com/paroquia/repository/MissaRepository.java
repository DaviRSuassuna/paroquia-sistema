package com.paroquia.repository;

import com.paroquia.model.Missa;
import com.paroquia.model.MissaRecorrente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MissaRepository implements PanacheRepository<Missa> {

    public List<Missa> findNaoCanceladas() {
        return list("cancelada", false);
    }

    public List<Missa> findByMissaRecorrente(MissaRecorrente missaRecorrente) {
        return list("missaRecorrente", missaRecorrente);
    }
}
