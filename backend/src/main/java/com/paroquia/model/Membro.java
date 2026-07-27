package com.paroquia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "membro")
public class Membro extends DefaultEntity {

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "nome_responsavel")
    private String nomeResponsavel;

    @Column(name = "telefone_responsavel", length = 20)
    private String telefoneResponsavel;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "anonimizado", nullable = false)
    private boolean anonimizado = false;

    @Column(name = "data_anonimizacao")
    private LocalDateTime dataAnonimizacao;

    @Column(name = "motivo_anonimizacao")
    private String motivoAnonimizacao;

    @Column(name = "ultima_resposta_disponibilidade")
    private LocalDateTime ultimaRespostaDisponibilidade;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "membro_pastoral",
        joinColumns = @JoinColumn(name = "membro_id"),
        inverseJoinColumns = @JoinColumn(name = "pastoral_id")
    )
    private Set<Pastoral> pastorais = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "membro_funcao",
        joinColumns = @JoinColumn(name = "membro_id"),
        inverseJoinColumns = @JoinColumn(name = "funcao_id")
    )
    private Set<Funcao> funcoes = new HashSet<>();

}