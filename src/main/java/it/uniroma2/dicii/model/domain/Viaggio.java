package it.uniroma2.dicii.model.domain;

import java.util.Date;

public class Viaggio {
    private int idViaggio;
    private Date dataPartenza;
    private Date dataRientro;
    private String stato;
    private int idItinerario;

    // Getters and Setters
    public int getIdViaggio() { return idViaggio; }
    public void setIdViaggio(int idViaggio) { this.idViaggio = idViaggio; }

    public Date getDataPartenza() { return dataPartenza; }
    public void setDataPartenza(Date dataPartenza) { this.dataPartenza = dataPartenza; }

    public Date getDataRientro() { return dataRientro; }
    public void setDataRientro(Date dataRientro) { this.dataRientro = dataRientro; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public int getIdItinerario() { return idItinerario; }
    public void setIdItinerario(int idItinerario) { this.idItinerario = idItinerario; }

    @Override
    public String toString() {
        return  "\n"+
                "  ID Viaggio: " + idViaggio + "\n" +
                "  Data Partenza: " + dataPartenza + "\n" +
                "  Data Rientro: " + dataRientro + "\n" +
                "  ID Itinerario: " + idItinerario + "\n" ;
    }
}