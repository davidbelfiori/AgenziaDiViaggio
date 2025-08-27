package it.uniroma2.dicii.model.domain;

public class Itinerario {
    private int idItinerario;
    private int durata;
    private float costo;
    private String nomeItinerario;

    // Getters and Setters
    public int getIdItinerario() { return idItinerario; }
    public void setIdItinerario(int idItinerario) { this.idItinerario = idItinerario; }

    public int getDurata() { return durata; }
    public void setDurata(int durata) { this.durata = durata; }

    public float getCosto() { return costo; }
    public void setCosto(float costo) { this.costo = costo; }

    public String getNomeItinerario() { return nomeItinerario; }
    public void setNomeItinerario(String nomeItinerario) { this.nomeItinerario = nomeItinerario; }

    @Override
    public String toString() {
       return  "\n"+
               "IDItinerario=" + idItinerario + "\n" +
               "Durata=" + durata +"\n" +
               "Costo=" + costo +"\n" +
               "NomeItinerario='" + nomeItinerario ;
    }
}