package com.paroquia.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pastoral")
public class Pastoral extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String nome;

    public String descricao;

    @Column(nullable = false)
    public boolean ativo = true;

    @Column(nullable = false)
    public LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToMany(mappedBy = "pastorais")
    public Set<Membro> membros;
}