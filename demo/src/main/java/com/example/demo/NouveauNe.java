package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NouveauNe {
    @JsonProperty("id_ne")
    private Integer id;
    private String prenomNouveauN;
    private String nomPere;
    private String prenomPere;
    private String cinPere;
    private String dateNaissance;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPrenomNouveauN() { return prenomNouveauN; }
    public void setPrenomNouveauN(String prenomNouveauN) { this.prenomNouveauN = prenomNouveauN; }

    public String getNomPere() { return nomPere; }
    public void setNomPere(String nomPere) { this.nomPere = nomPere; }

    public String getPrenomPere() { return prenomPere; }
    public void setPrenomPere(String prenomPere) { this.prenomPere = prenomPere; }

    public String getCinPere() { return cinPere; }
    public void setCinPere(String cinPere) { this.cinPere = cinPere; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
}
