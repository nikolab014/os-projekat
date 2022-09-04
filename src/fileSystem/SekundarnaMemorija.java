package fileSystem;

import java.util.ArrayList;

//Klasa koja predstavlja simulaciju sekundarne memorije koristeci NEPREKIDNO ALOCIRANJE.

public class SekundarnaMemorija {
	
	private int BROJ_BLOKOVA;
	private Blok[] blokovi;
	// 0 - slobodan blok, 1 - zauzet blok, pracenje blokova se vrsi pomocu bitmape
	private static final int ZAUZET_BLOK = 1;
	private static final int SLOBODAN_BLOK = 0;
	private int[] nizSlobodnihBlokova;
	private char[] nizPodataka;
	
	//Konstruktor koji inicijalizuje instancu sekundarne memorije sa brBlok blokova.
	public SekundarnaMemorija(int brBlok) {
		this.BROJ_BLOKOVA = brBlok;
		this.nizPodataka = new char[this.BROJ_BLOKOVA*Blok.getVelicina()];
		this.nizSlobodnihBlokova = new int[BROJ_BLOKOVA];
		this.blokovi = new Blok[BROJ_BLOKOVA];
		for(int i=0; i<BROJ_BLOKOVA; i++) {
			this.blokovi[i] = new Blok(i*Blok.getVelicina());
			this.nizSlobodnihBlokova[i] = SLOBODAN_BLOK;
			for(int j=0; j<4; j++) 
				this.nizPodataka[i+j]=Character.MIN_VALUE;
		}
	}

	/* Funkcija traži K uzastopnih blokova. Ako ih pronadje, zauzima ih i zapisuje na njih sadrzaj datoteke.
	 * Vraca novonastalu datoteku. Vraca null ako ne pronadje potrebni prostor.
	 */
	public synchronized Datoteka kreirajDatoteku(String sadrzaj) {
		int k = sadrzaj.length() / Blok.getVelicina();
		if(sadrzaj.length() % Blok.getVelicina() != 0)
			k += 1;
		ArrayList<Blok> zauzetiBlokovi = new ArrayList<>();
		for(int i=0; i<BROJ_BLOKOVA-k; i++) {
			if(nizSlobodnihBlokova[i] == SLOBODAN_BLOK) {
				boolean uslov = true;
				int j = 0;
				for(; j<k; j++)
					if(nizSlobodnihBlokova[i+j] == ZAUZET_BLOK) {
						uslov = false;
						break;
					}
				
				for(int j2=0; j2<k && uslov; j2++) {
					nizSlobodnihBlokova[i+j2] = ZAUZET_BLOK;
					zauzetiBlokovi.add(blokovi[i+j2]);
				}
				
				if(uslov) {
					break;
				} else {
					i += j;
				}
					
			}
		}
		if(zauzetiBlokovi.size() == k) {
			int start = zauzetiBlokovi.get(0).getAdresa();
			int end = zauzetiBlokovi.get(zauzetiBlokovi.size()-1).getAdresa();
			for(int i=0; i<4; i++)
				this.nizPodataka[end+i] = Character.MIN_VALUE;
			for(char c: sadrzaj.toCharArray()) {
				this.nizPodataka[start] = c;
				start += 1;
				if(start > end + 3)
					break;
			}
			Datoteka rezultat = new Datoteka(k);
			rezultat.setBlokovi(zauzetiBlokovi);
			
			return rezultat;
		}
		System.err.println("Couldn't create file");
		return null;
	}
	
	//Funkcija za oslobodjenje blokova datoteke u bitmapi
	public void brisanjeDatoteke(Datoteka datoteka) {
		ArrayList<Blok> datotekaBlokovi = datoteka.getBlokovi();
		for(Blok b:datotekaBlokovi)
			nizSlobodnihBlokova[b.getAdresa()/Blok.getVelicina()] = SLOBODAN_BLOK;
	}
	
	//Funkcija za ispis stanja svih blokova u konzolu
	public void ispis() {
		System.out.print("|");
		for(int i=1; i<=BROJ_BLOKOVA; i++) {
			if(nizSlobodnihBlokova[i-1] == ZAUZET_BLOK)
				System.out.print("x");
			else
				System.out.print(" ");
			if(i % 100 == 0 && i == ZAUZET_BLOK)
				System.out.print("|\n|");
		}
		System.out.println("|");
	}
	
	//Funkcija za citanje sadrzaja datoteke, vraca String
	public String sadrzajDatoteke(Datoteka datoteka) {
		String rez = "";
		if(datoteka.getBlokovi().isEmpty())
			return rez;
		int start = datoteka.getBlokovi().get(0).getAdresa();
		int end = datoteka.getBlokovi().get(datoteka.getBlokovi().size()-1).getAdresa()+4;
		for(int i=start; i<end; i++)
			rez += this.nizPodataka[i];
		return rez;
	}
	
	
}
