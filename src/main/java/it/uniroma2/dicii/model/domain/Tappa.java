package it.uniroma2.dicii.model.domain;

public class Tappa {
    private int idItinerario;
    private String nomeLocalita;
    private String stato;
    private int ordine;
    private int giorni; // Può essere null

    // Getters and Setters
    public int getIdItinerario() { return idItinerario; }
    public void setIdItinerario(int idItinerario) { this.idItinerario = idItinerario; }

    public String getNomeLocalita() { return nomeLocalita; }
    public void setNomeLocalita(String nomeLocalita) { this.nomeLocalita = nomeLocalita; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public int getOrdine() { return ordine; }
    public void setOrdine(int ordine) { this.ordine = ordine; }

    public Integer getGiorni() { return giorni; }
    public void setGiorni(Integer giorni) { this.giorni = giorni; }
}