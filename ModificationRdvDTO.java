package com.projet.DocCarePortal.Entity;

import java.time.LocalDateTime;

public class ModificationRdvDTO {
    private LocalDateTime dateHeure;
    private String modeConsultation;

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getModeConsultation() {
        return modeConsultation;
    }

    public void setModeConsultation(String modeConsultation) {
        this.modeConsultation = modeConsultation;
    }
}
