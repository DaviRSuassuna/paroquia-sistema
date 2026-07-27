package com.paroquia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "configuracao_escalonamento")
public class ConfiguracaoEscalonamento extends DefaultEntity {

    @OneToOne
    @JoinColumn(name = "pastoral_id", nullable = false, unique = true)
    private Pastoral pastoral;

    @Column(name = "ordem_convocacao_reforco")
    private Integer ordemConvocacaoReforco;

    @Column(name = "intervalo_minutos_escalonamento")
    private Integer intervaloMinutosEscalonamento;

    @Column(name = "permite_convocacao_reforco", nullable = false)
    private boolean permiteConvocacaoReforco = false;

    @Column(name = "dia_geracao_mensal")
    private Integer diaGeracaoMensal;

    @Column(name = "prioridade_ativa", nullable = false)
    private boolean prioridadeAtiva = false;

    @Column(name = "limite_missa_semana_comum")
    private Integer limiteMissaSemanaComum;

    @Column(name = "limite_missa_semana_solene")
    private Integer limiteMissaSemanaSolene;

    @Column(name = "limite_missa_dominical_comum")
    private Integer limiteMissaDominicalComum;

    @Column(name = "limite_missa_dominical_solene")
    private Integer limiteMissaDominicalSolene;
    
}