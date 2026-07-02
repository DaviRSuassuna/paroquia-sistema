package com.paroquia.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missa")
public class Missa extends PanacheEntity {

    @Column(nullable = false)
    public LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TipoMissa tipo;

    @Column(nullable = false)
    public boolean solene = false;

    @Column(nullable = false)
    public LocalDateTime dataCriacao = LocalDateTime.now();
}