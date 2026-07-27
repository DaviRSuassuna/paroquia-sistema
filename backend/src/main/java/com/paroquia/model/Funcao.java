package com.paroquia.model;

import com.paroquia.model.enums.Complexidade;
import com.paroquia.model.enums.TipoMissa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "funcao")
public class Funcao extends DefaultEntity {

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "somente_solene", nullable = false)
    private boolean somenteSolene = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexidade", nullable = false, length = 20)
    private Complexidade complexidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_minimo", nullable = false, length = 20)
    private TipoMissa tipoMinimo;

    @ManyToOne
    @JoinColumn(name = "pastoral_id", nullable = false)
    private Pastoral pastoral;

}