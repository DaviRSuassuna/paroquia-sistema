package com.paroquia.repository;

import com.paroquia.model.FormularioDisponibilidade;
import com.paroquia.model.Pastoral;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.YearMonth;
import java.util.Optional;

@ApplicationScoped
public class FormularioDisponibilidadeRepository implements PanacheRepository<FormularioDisponibilidade> {

    public Optional<FormularioDisponibilidade> findByPastoralEMes(Pastoral pastoral, YearMonth mesReferencia) {
        return find("pastoral = ?1 and mesReferencia = ?2", pastoral, mesReferencia).firstResultOptional();
    }
}
