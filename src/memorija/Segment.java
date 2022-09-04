package memorija;

import java.util.ArrayList;

//Klasa koja predstavlja jedan segment
public class Segment {
	
	private static final int najvecaVelicina = 1024; //Najveca duzina sadrzaja u bitima
	private int baza; //Pocetak fizicke adrese segmenta u odnosu na slobodne blokove memorije
	private int duzina; //Broj bita sadrzaja
	private ArrayList<String> sadrzaj; //Svaki element sadrzaja je 16 bita
	
	//Konstruktor koji prima bazu, tj. pocetak segmenta na fizickoj memoriji
	public Segment(int baza) {
		this.setBaza(baza);
		this.sadrzaj = new ArrayList<String>();
		this.setDuzina(0);
	}
	
	//Funkcija koja segmentu dodaje sadrzaj od 16 bita
	public void dodajSadrzaj2B(String s) {
		this.setDuzina(this.getDuzina() + 16);
		this.sadrzaj.add(s);
	}

	public int getDuzina() {
		return duzina;
	}

	public void setDuzina(int duzina) {
		this.duzina = duzina;
	}

	public int getBaza() {
		return baza;
	}

	public void setBaza(int baza) {
		this.baza = baza;
	}
	
	public ArrayList<String> getSadrzaj(){
		return this.sadrzaj;
	}
	
	public String toString() {
		return "" + this.baza + " " + this.duzina;
	}

}
