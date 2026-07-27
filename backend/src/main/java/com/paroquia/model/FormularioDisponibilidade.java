package com.paroquia.model;

import com.paroquia.model.converter.YearMonthConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "formulario_disponibilidade")
public class FormularioDisponibilidade extends DefaultEntity {

    @Convert(converter = YearMonthConverter.class)
    @Column(name = "mes_referencia", nullable = false, length = 7)
    private YearMonth mesReferencia;

    @Column(name = "link_google_forms")
    private String linkGoogleForms;

    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "minimo_missas_mensal")
    private Integer minimoMissasMensal;

    @ManyToOne
    @JoinColumn(name = "pastoral_id", nullable = false)
    private Pastoral pastoral;

}