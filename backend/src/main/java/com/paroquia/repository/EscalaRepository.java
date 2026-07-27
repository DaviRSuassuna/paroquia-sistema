package com.paroquia.repository;

import com.paroquia.model.Escala;
import com.paroquia.model.Membro;
import com.paroquia.model.Missa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EscalaRepository implements PanacheRepository<Escala> {

    public List<Escala> findByMissa(Missa missa) {
        return list("missa", missa);
    }

    public List<Escala> findByMembro(Membro membro) {
        return list("membro", membro);
    }
}
