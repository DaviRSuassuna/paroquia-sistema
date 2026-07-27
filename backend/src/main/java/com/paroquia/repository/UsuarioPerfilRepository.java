package com.paroquia.repository;

import com.paroquia.model.Usuario;
import com.paroquia.model.UsuarioPerfil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UsuarioPerfilRepository implements PanacheRepository<UsuarioPerfil> {

    public List<UsuarioPerfil> findByUsuario(Usuario usuario) {
        return list("usuario", usuario);
    }
}
