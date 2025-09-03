package it.uniroma2.dicii.model.domain;

public class AssociazioneAutobusViaggioResult {
    private final String targa;
    private final int codiceViaggio;
    private final String messaggio;

    public AssociazioneAutobusViaggioResult(String targa, int codiceViaggio, String messaggio) {
        this.targa = targa;
        this.codiceViaggio = codiceViaggio;
        this.messaggio = messaggio;
    }

    public String getTarga() { return targa; }
    public int getCodiceViaggio() { return codiceViaggio; }
    public String getMessaggio() { return messaggio; }

    @Override
    public String toString() {
        return '\n' +
                "Targa='" + targa + '\n' +
                "Codice Viaggio=" + codiceViaggio + '\n' +
                "Messaggio='" + messaggio;
    }
}
