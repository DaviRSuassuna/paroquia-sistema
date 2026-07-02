package com.paroquia.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "funcao")
public class Funcao extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String nome;

    public String descricao;

    @Column(nullable = false)
    public boolean ativo = true;

    // Restrições de elegibilidade da função
    @Column(nullable = false)
    public boolean somenteSolene = false; // ex: Turiferário, Naviculário

    @ElementCollection(targetClass = TipoMissa.class)
    @CollectionTable(name = "funcao_tipo_missa_permitido",
        joinColumns = @JoinColumn(name = "funcao_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_missa")
    public Set<TipoMissa> tiposMissaPermitidos; // vazio/null = todos os tipos permitidos

    @Column(nullable = false)
    public LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "funcao_pastoral",
        joinColumns = @JoinColumn(name = "funcao_id"),
        inverseJoinColumns = @JoinColumn(name = "pastoral_id"))
    public Set<Pastoral> pastorais;

    @ManyToMany(mappedBy = "funcoes")
    public Set<Membro> membros;
}