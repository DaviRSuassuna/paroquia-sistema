package com.paroquia.repository;

import com.paroquia.model.Membro;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MembroRepository implements PanacheRepository<Membro> {

    public List<Membro> findAtivos() {
        return list("ativo", true);
    }
}
