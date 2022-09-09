package fileSystem;

import java.util.ArrayList;

//Klasa koja predstavlja datoteku, tj. objekat koji okupira nekoliko blokova memorije.

public class Datoteka {
    private int velicina;
    private ArrayList<Blok> blokovi = new ArrayList<>();

    public Datoteka(int velicina) {
        this.velicina = velicina;
    }

    public ArrayList<Blok> getBlokovi() {
        return blokovi;
    }

    public void setBlokovi(ArrayList<Blok> blokovi) {
        this.blokovi = blokovi;
    }

    public int getVelicina() {
        return velicina;
    }
}
