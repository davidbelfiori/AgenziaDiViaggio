package it.uniroma2.dicii.model.domain;

public class Autobus {
    private String targa;
    private int capienza;
    private float costoForfettario;

    // Getters and Setters
    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }

    public int getCapienza() { return capienza; }
    public void setCapienza(int capienza) { this.capienza = capienza; }

    public float getCostoForfettario() { return costoForfettario; }
    public void setCostoForfettario(float costoForfettario) { this.costoForfettario = costoForfettario; }

    @Override
    public String toString() {
        return "Autobus{" +
                "targa='" + targa + '\'' +
                ", capienza=" + capienza +
                ", costoForfettario=" + costoForfettario +
                '}';
    }
}