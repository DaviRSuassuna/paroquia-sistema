package com.paroquia.repository;

import com.paroquia.model.ConvocacaoReforco;
import com.paroquia.model.enums.StatusConvocacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ConvocacaoReforcoRepository implements PanacheRepository<ConvocacaoReforco> {

    public Optional<ConvocacaoReforco> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public List<ConvocacaoReforco> findByStatus(StatusConvocacao status) {
        return list("status", status);
    }
}
