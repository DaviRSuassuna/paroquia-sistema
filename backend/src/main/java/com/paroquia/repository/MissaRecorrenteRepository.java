package com.paroquia.repository;

import com.paroquia.model.MissaRecorrente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.DayOfWeek;
import java.util.List;

@ApplicationScoped
public class MissaRecorrenteRepository implements PanacheRepository<MissaRecorrente> {

    public List<MissaRecorrente> findAtivas() {
        return list("ativo", true);
    }

    public List<MissaRecorrente> findByDiaSemana(DayOfWeek diaSemana) {
        return list("diaSemana", diaSemana);
    }
}
