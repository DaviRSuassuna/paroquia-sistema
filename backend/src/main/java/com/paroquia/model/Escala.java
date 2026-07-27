package com.paroquia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "escala",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_escala_missa_membro_funcao",
        columnNames = {"missa_id", "membro_id", "funcao_id"}
    )
)
public class Escala extends DefaultEntity {

    @ManyToOne
    @JoinColumn(name = "missa_id", nullable = false)
    private Missa missa;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membro membro;

    @ManyToOne
    @JoinColumn(name = "funcao_id", nullable = false)
    private Funcao funcao;

}