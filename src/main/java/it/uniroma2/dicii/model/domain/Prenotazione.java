package it.uniroma2.dicii.model.domain;

public class Prenotazione {
    private int codicePrenotazione;
    private int numeroPartecipanti;
    private String codiceDisdetta;
    private int codiceViaggio;

    // Getters and Setters
    public int getCodicePrenotazione() { return codicePrenotazione; }
    public void setCodicePrenotazione(int codicePrenotazione) { this.codicePrenotazione = codicePrenotazione; }

    public int getNumeroPartecipanti() { return numeroPartecipanti; }
    public void setNumeroPartecipanti(int numeroPartecipanti) { this.numeroPartecipanti = numeroPartecipanti; }

    public String getCodiceDisdetta() { return codiceDisdetta; }
    public void setCodiceDisdetta(String codiceDisdetta) { this.codiceDisdetta = codiceDisdetta; }

    public int getCodiceViaggio() { return codiceViaggio; }
    public void setCodiceViaggio(int codiceViaggio) { this.codiceViaggio = codiceViaggio; }
}