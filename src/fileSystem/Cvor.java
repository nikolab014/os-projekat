package fileSystem;

import java.util.ArrayList;
import java.util.Collections;

//Klasa koja predstavlja cvor u drvolikom fajl sistemu
public class Cvor {
	
	private String nazivDatoteke;
	private boolean dir; 
	private Datoteka datoteka;
	private Cvor roditelj;
	private ArrayList<Cvor> potomci;
	
	//Konstruktor za FAJL. Baca OutOfMemoryError ako se datoteka neuspjesno napravi.
	public Cvor(String nazivDatoteke, String sadrzajDatoteke, Cvor roditelj, SekundarnaMemorija sm) throws OutOfMemoryError {
		this.setRoditelj(roditelj);
		this.nazivDatoteke = nazivDatoteke;
		this.dir = false;
		this.potomci = new ArrayList<>();
		this.datoteka = sm.kreirajDatoteku(sadrzajDatoteke);
		if(this.datoteka == null)
			throw new OutOfMemoryError("Insufficient memory to create file.");
	}

	//Konstruktor za FOLDER. Baca OutOfMemoryError ako se datoteka neuspjesno napravi.
	public Cvor(String nazivDatoteke, Cvor roditelj, SekundarnaMemorija sm) throws OutOfMemoryError {
		this.setRoditelj(roditelj);
		this.nazivDatoteke = nazivDatoteke;
		this.dir = true;
		this.potomci = new ArrayList<>();
		this.datoteka = sm.kreirajDatoteku("dir");
		if(this.datoteka == null)
			throw new OutOfMemoryError("Insufficient memory to create file.");
		
	}
	
	public String toString() {
		String rez = "";
		ArrayList<String> tempStringList = new ArrayList<>();
		Cvor tempCvor = this;
		do {
			tempStringList.add(tempCvor.getNaziv());
			tempCvor = tempCvor.getRoditelj();
		}while (tempCvor != null);
		Collections.reverse(tempStringList);
		for(String str: tempStringList) 
			rez = rez + str + "\\";
		//rez += ">";
		
		return rez;
	}
	
	public ArrayList<Cvor> getPotomci() {
		return potomci;
	}

	public Cvor getRoditelj() {
		return roditelj;
	}

	public void setRoditelj(Cvor roditelj) {
		this.roditelj = roditelj;
	}

	public String getNaziv() {
		return this.nazivDatoteke;
	}
	
	public void setNaziv(String naziv) {
		this.nazivDatoteke = naziv;
	}

	public boolean isDir() {
		return dir;
	}

	public void setDir(boolean dir) {
		this.dir = dir;
	}
	
	public Datoteka getDatoteka() {
		return this.datoteka;
	}
	
}





















