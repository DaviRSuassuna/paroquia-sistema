package com.paroquia.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "membro")
public class Membro extends PanacheEntity {

    @Column(nullable = false)
    public String nomeCompleto;

    @Column(nullable = false)
    public LocalDate dataNascimento;

    @Column(nullable = false)
    public String telefone;

    public String nomeResponsavel;

    public String telefoneResponsavel;

    @Column(nullable = false)
    public boolean ativo = true;

    @Column(nullable = false)
    public LocalDateTime dataCadastro = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "membro_pastoral",
        joinColumns = @JoinColumn(name = "membro_id"),
        inverseJoinColumns = @JoinColumn(name = "pastoral_id"))
    public Set<Pastoral> pastorais;

    @ManyToMany
    @JoinTable(name = "membro_funcao",
        joinColumns = @JoinColumn(name = "membro_id"),
        inverseJoinColumns = @JoinColumn(name = "funcao_id"))
    public Set<Funcao> funcoes;
}