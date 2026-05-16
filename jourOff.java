package com.projet.DocCarePortal.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Data
public class JourOff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate dateOff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;


    public JourOff() {}


    public JourOff(LocalDate dateOff, Medecin medecin) {
        this.dateOff = dateOff;
        this.medecin = medecin;
    }

}
