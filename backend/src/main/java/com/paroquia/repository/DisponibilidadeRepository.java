package com.paroquia.repository;

import com.paroquia.model.Disponibilidade;
import com.paroquia.model.FormularioDisponibilidade;
import com.paroquia.model.Membro;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DisponibilidadeRepository implements PanacheRepository<Disponibilidade> {

    public List<Disponibilidade> findByMembro(Membro membro) {
        return list("membro", membro);
    }

    public List<Disponibilidade> findByFormulario(FormularioDisponibilidade formularioDisponibilidade) {
        return list("formularioDisponibilidade", formularioDisponibilidade);
    }
}
