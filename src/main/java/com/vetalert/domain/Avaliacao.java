// @author Paulo Pacifico

package com.vetalert.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmaco;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private Integer totalRelatos;

    @Column(nullable = false)
    private Integer relatosGraves;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRisco nivel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "avaliacao_reacoes", joinColumns = @JoinColumn(name = "avaliacao_id"))
    @Column(name = "reacao")
    private List<String> reacoesTop = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    protected Avaliacao() {
    }

    public Avaliacao(String farmaco, String especie, Integer totalRelatos, Integer relatosGraves,
                     Double score, NivelRisco nivel, List<String> reacoesTop) {
        this.farmaco = farmaco;
        this.especie = especie;
        this.totalRelatos = totalRelatos;
        this.relatosGraves = relatosGraves;
        this.score = score;
        this.nivel = nivel;
        this.reacoesTop = reacoesTop == null ? new ArrayList<>() : reacoesTop;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public String getEspecie() {
        return especie;
    }

    public Integer getTotalRelatos() {
        return totalRelatos;
    }

    public Integer getRelatosGraves() {
        return relatosGraves;
    }

    public Double getScore() {
        return score;
    }

    public NivelRisco getNivel() {
        return nivel;
    }

    public List<String> getReacoesTop() {
        return reacoesTop;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
