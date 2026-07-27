package com.paroquia.model;

import com.paroquia.model.enums.StatusConvocacao;
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
@Table(name = "convocacao_reforco")
public class ConvocacaoReforco extends DefaultEntity {

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusConvocacao status;

    @Column(name = "onda", nullable = false)
    private int onda;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @ManyToOne
    @JoinColumn(name = "pastoral_id", nullable = false)
    private Pastoral pastoral;

    @ManyToOne
    @JoinColumn(name = "escala_id", nullable = false)
    private Escala escala;

    @ManyToOne
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

}