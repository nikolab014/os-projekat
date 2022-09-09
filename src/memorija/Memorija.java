package memorija;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import proces.Proces;
import proces.ProcesScheduler;

/* Klasa za glavnu memoriju koristeci segmentaciju. Velicina memorije je 4Kib. Koristi se prosta segmentacija, prilikom ucitavanja
 * procesa ucitavaju se svi njegovi segmenti u glavnu memoriju. Segmenti su dinamicke velicine i sastoje se od nekoliko blokova. 
 * U procesima se cuvaju segment tabele preko kojih se dolazi do fizicke adrese segmenata. 
 */
public class Memorija {
	
	private static int velicina = 4096; //RAM od 4096b
	private static int zauzeto = 0;
	private static int[] nizSlobodnihBlokova = new int[128]; //Broj memorijskih blokova od po 16b, odnosno 2B, 0=Slobodan 1=Zauzet
	//private static Queue<Proces> readyQueue = new LinkedList<>();
	//private static Proces aktivniProces = null;
	
	/* Funkcija uzima proces sa sadrzajem, pravi segmente sa navedenim sadrzajem uzimajuci u obzir slobodan prostor na memoriji i 
	 * najvecu dozvoljenu velicinu segmenta
	 * Vraca ArrayList segmenata ili praznu ArrayListu ako su podaci prazni ili nema dovoljno memorije.
	 * Potrebno je postaviti tabelu segmenta procesa na ovu return vrijednost, ili napraviti manju izmjenu.
	 */
	public static synchronized ArrayList<Segment> ucitajProces(Proces proces){
		ArrayList<String> kod = proces.getCodeAndData();
		if(kod.size() > (velicina-zauzeto)/16) {
			System.out.println("Unable to load process - insufficient memory.");
			return new ArrayList<Segment>(); //U slucaju previse koda tj. premalo memorije
		}
		zauzeto += kod.size() * 16;
		ArrayList<Segment> rezultat = new ArrayList<Segment>();
		int brojac = 0;
		Segment sm = null;
		for(int i=0; i<Memorija.nizSlobodnihBlokova.length; i++)
			if(Memorija.nizSlobodnihBlokova[i] == 0) {
				sm = new Segment(i);
				sm.dodajSadrzaj2B(kod.get(brojac));
				brojac += 1;
				Memorija.nizSlobodnihBlokova[i] = 1;
				break;
			}
		if(sm != null)
			for(int i=sm.getBaza()+1; i<Memorija.nizSlobodnihBlokova.length; i++) {
				if(brojac >= kod.size()) {
					rezultat.add(sm);
					break;
				}
				if(Memorija.nizSlobodnihBlokova[i] == 1) {
					if(sm.getDuzina() == 0)
						continue;
					else{
						rezultat.add(sm);
						sm = new Segment(i);
					}
				}else{
					if(sm.getDuzina() == 0) 
						sm.setBaza(i);
					sm.dodajSadrzaj2B(kod.get(brojac));
					brojac += 1;
					Memorija.nizSlobodnihBlokova[i] = 1;
				}	
			}
		//ProcesScheduler.getReadyQueue().add(proces);
		//proces.setState("READY");
		return rezultat;
	}
	
	/* Funkcija za oslobadjanje prostora koje su zauzeli segmenti procesa. Ne brise segmente iz tabele u procesu, 
	 * samo oslobadja memoriju.
	 */
	public static void oslobodiMemoriju(Proces proces) {
		for(Segment s: proces.getTabela()) {
			//System.out.println("Oslobadja se");
			//System.out.println(s.toString());
			zauzeto -= s.getDuzina();
			for(int i = 0; i*16 < s.getDuzina(); i++) {
				Memorija.nizSlobodnihBlokova[i+s.getBaza()] = 0;
			}
		}
	}
	
	/*
	 * Funkcija za prevodjenje logicke adrese u fizicku (za load i store).
	 * Funkcija uzima logicku adresu od 16 bita oblika "SSSSSSOOOOOOOOOO" i tabelu segmenata procesa.
	 * SSSSSS je 6 bita koji predstavljaju binarnu vrijednost rednog broja segmenta u tabeli procesa.
	 * OOOOOOOOOO je 10 bita koji predstavljaju binarnu vrijednost offset.
	 * Npr ako su u pitanju 0 i 32, odnose se na drugi(32/16) clan liste "sadrzaj" segmenta tabela.get(0).
	 * Vraca se ArrayList<Integer> gdje je prvi clan broj segmenta u tabeli a drugi broj clana liste "sadrzaj".
	 * U slucaju pogresne adrese vraca se null.
	 */
	
	//Ekstra komentar: vjerovatno samo kod load/store da uradis segmentTabela.get(brSegmenta).get/.set(indeks)?
	//Jer kako sam ja ovo realizovao na kraju, stvarni podaci se svi nalaze u obliku stringova u listama u segmentima
	//A svaki od tih stringova je po 16 bita TODO: obrisati ovaj komentar
	//Segmentacija je grozna. Literatura nepostojeca. 
	public static ArrayList<Integer> logAdrPrevod(ArrayList<Segment> tabela, String adresa){
		ArrayList<Integer> rez = new ArrayList<Integer>();
		String segment = adresa.substring(0, 6);
		String offset = adresa.substring(6);
		int brSegmenta = Integer.parseInt(segment, 2);
		int indeks = Integer.parseInt(offset, 2)/16;
		if(brSegmenta < tabela.size() && indeks<tabela.get(brSegmenta).getSadrzaj().size()) {
			rez.add(brSegmenta);
			rez.add(indeks);
			return rez;
		}
		return null;
	}

	public static int getVelicina() {
		return velicina;
	}

	public static int powerOfTwo(int size) {
		int i=1;
		int counter=0;
		while(i<=size) {
			i*=2;
			counter++;
		}
		if (i/2 == size)
			return --counter;
		return -1;
	}

	public static String decToBinary(int n){
		String binaryNumber="";
		int[] binaryNum = new int[1000];
		int i = 0;

		while (n > 0) {
			binaryNum[i] = n % 2;
			n = n / 2;
			i++;
		}
		for (int j = i - 1; j >= 0; j--)
			binaryNumber+=String.valueOf(binaryNum[j]);

		return binaryNumber;
	}

}
