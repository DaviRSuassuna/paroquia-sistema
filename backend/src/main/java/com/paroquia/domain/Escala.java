package com.paroquia.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "escala",
    uniqueConstraints = @UniqueConstraint(columnNames = {"missa_id", "membro_id", "funcao_id"}))
public class Escala extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "missa_id")
    public Missa missa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "membro_id")
    public Membro membro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcao_id")
    public Funcao funcao;

    // null = não registrado, true = presente, false = ausente
    public Boolean presenca;

    @Column(nullable = false)
    public boolean duplaFuncao = false;

    @Column(nullable = false)
    public LocalDateTime dataCriacao = LocalDateTime.now();
}