package com.paroquia.repository;

import com.paroquia.model.Membro;
import com.paroquia.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public Optional<Usuario> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResultOptional();
    }

    public Optional<Usuario> findByMembro(Membro membro) {
        return find("membro", membro).firstResultOptional();
    }
}
