package it.uniroma2.dicii.model.domain;

import java.util.ArrayList;
import java.util.List;

public class ItinerarioConTappe extends Itinerario {

    private List<Tappa> tappe;

    public ItinerarioConTappe() {
        this.tappe = new ArrayList<>();
    }

    public List<Tappa> getTappe() {
        return tappe;
    }

    public void setTappe(List<Tappa> tappe) {
        this.tappe = tappe;
    }



}
