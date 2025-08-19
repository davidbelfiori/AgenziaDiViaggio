package it.uniroma2.dicii.model.domain;

import java.util.Date;

public class Pernottamento {
    private int idViaggio;
    private int codiceAlbergo;
    private String nomeLocalita;
    private String stato;
    private Date dataCheckIn;
    private Date dataCheckOut;

    // Getters and Setters
    public int getIdViaggio() { return idViaggio; }
    public void setIdViaggio(int idViaggio) { this.idViaggio = idViaggio; }

    public int getCodiceAlbergo() { return codiceAlbergo; }
    public void setCodiceAlbergo(int codiceAlbergo) { this.codiceAlbergo = codiceAlbergo; }

    public String getNomeLocalita() { return nomeLocalita; }
    public void setNomeLocalita(String nomeLocalita) { this.nomeLocalita = nomeLocalita; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Date getDataCheckIn() { return dataCheckIn; }
    public void setDataCheckIn(Date dataCheckIn) { this.dataCheckIn = dataCheckIn; }

    public Date getDataCheckOut() { return dataCheckOut; }
    public void setDataCheckOut(Date dataCheckOut) { this.dataCheckOut = dataCheckOut; }
}