package com.projet.DocCarePortal.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TriageResponse {

    private String urgence;
    private String specialite;
    private String specialiste;
    private String action;
    private String explication;

    @JsonProperty("signes_alerte")
    private List<String> signesAlerte;

    private List<Medecin> medecins;

    public String getUrgence() { return urgence; }
    public void setUrgence(String urgence) { this.urgence = urgence; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getSpecialiste() { return specialiste; }
    public void setSpecialiste(String specialiste) { this.specialiste = specialiste; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getExplication() { return explication; }
    public void setExplication(String explication) { this.explication = explication; }

    public List<String> getSignesAlerte() { return signesAlerte; }
    public void setSignesAlerte(List<String> signesAlerte) { this.signesAlerte = signesAlerte; }

    public List<Medecin> getMedecins() { return medecins; }
    public void setMedecins(List<Medecin> medecins) { this.medecins = medecins; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TriageResponse r = new TriageResponse();

        public Builder urgence(String v)           { r.urgence = v;      return this; }
        public Builder specialite(String v)        { r.specialite = v;   return this; }
        public Builder specialiste(String v)       { r.specialiste = v;  return this; }
        public Builder action(String v)            { r.action = v;       return this; }
        public Builder explication(String v)       { r.explication = v;  return this; }
        public Builder signesAlerte(List<String> v){ r.signesAlerte = v; return this; }
        public Builder medecins(List<Medecin> v)   { r.medecins = v;     return this; }
        public TriageResponse build()              { return r; }
    }
}
