package com.paroquia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "disponibilidade")
public class Disponibilidade extends DefaultEntity {

    @ManyToOne
    @JoinColumn(name = "formulario_disponibilidade_id", nullable = false)
    private FormularioDisponibilidade formularioDisponibilidade;

    @ManyToOne
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "disponibilidade_missa_recorrente",
        joinColumns = @JoinColumn(name = "disponibilidade_id"),
        inverseJoinColumns = @JoinColumn(name = "missa_recorrente_id")
    )
    private Set<MissaRecorrente> missasRecorrentes = new HashSet<>();

}