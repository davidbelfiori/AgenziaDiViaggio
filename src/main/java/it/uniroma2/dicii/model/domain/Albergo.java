package it.uniroma2.dicii.model.domain;

public class Albergo {
    private int codiceAlbergo;
    private String nome;
    private int capienza;
    private float costoPN;
    private String referente;
    private String via;
    private String numero;
    private String cap;
    private String email;
    private String fax;
    private String nomeLocalita;
    private String stato;

    public Albergo() {
    }

    public Albergo(int codiceAlbergo, String nome, int capienza, float costoPN, String referente, String via, String numero, String cap, String email, String fax, String nomeLocalita, String stato) {
        this.codiceAlbergo = codiceAlbergo;
        this.nome = nome;
        this.capienza = capienza;
        this.costoPN = costoPN;
        this.referente = referente;
        this.via = via;
        this.numero = numero;
        this.cap = cap;
        this.email = email;
        this.fax = fax;
        this.nomeLocalita = nomeLocalita;
        this.stato = stato;
    }

    // Getters and Setters
    public int getCodiceAlbergo() { return codiceAlbergo; }
    public void setCodiceAlbergo(int codiceAlbergo) { this.codiceAlbergo = codiceAlbergo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapienza() { return capienza; }
    public void setCapienza(int capienza) { this.capienza = capienza; }

    public float getCostoPN() { return costoPN; }
    public void setCostoPN(float costoPN) { this.costoPN = costoPN; }

    public String getReferente() { return referente; }
    public void setReferente(String referente) { this.referente = referente; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getNomeLocalita() { return nomeLocalita; }
    public void setNomeLocalita(String nomeLocalita) { this.nomeLocalita = nomeLocalita; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    @Override
    public String toString() {
        return "Albergo {\n" +
                "  Nome: " + nome + "\n" +
                "  Capienza: " + capienza + "\n" +
                "  Costo per Notte: " + costoPN + "\n" +
                "  Referente: " + referente + "\n" +
                "  Indirizzo: " + via + ", " + numero + ", " + cap + "\n" +
                "  Località: " + nomeLocalita + ", " + stato + "\n" +
                "  Email: " + email + "\n" +
                "  Fax: " + fax + "\n" +
                '}';
    }
}