package it.uniroma2.dicii.model.domain;

public class Localita {
    private String nome;
    private String stato;
    private String provincia;
    private String regione;

    // Getters and Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getRegione() { return regione; }
    public void setRegione(String regione) { this.regione = regione; }

    @Override
    public String toString() {
        return "Localita{" +
                "nome='" + nome + '\'' +
                ", stato='" + stato + '\'' +
                ", provincia='" + provincia + '\'' +
                ", regione='" + regione + '\'' +
                '}';
    }
}