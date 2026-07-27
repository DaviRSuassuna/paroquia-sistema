package com.paroquia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "configuracao_sistema")
public class ConfiguracaoSistema extends DefaultEntity {

    @Column(name = "prazo_antecedencia_reforco_horas", nullable = false)
    private Integer prazoAntecedenciaReforcoHoras;

    @Column(name = "prazo_lembrete_horas", nullable = false)
    private Integer prazoLembreteHoras;

}