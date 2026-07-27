package com.paroquia.model;

import com.paroquia.model.enums.TipoMissa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "missa")
public class Missa extends DefaultEntity {

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMissa tipo;

    @Column(name = "solene", nullable = false)
    private boolean solene = false;

    @Column(name = "cancelada", nullable = false)
    private boolean cancelada = false;

    @ManyToOne
    @JoinColumn(name = "missa_recorrente_id")
    private MissaRecorrente missaRecorrente;
    
}