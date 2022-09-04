package komandnaLinija;

import java.util.Scanner;

import fileSystem.FileStablo;
import fileSystem.SekundarnaMemorija;


//Klasa koja predstavlja komandnu liniju
public class CMD {
	
	//Spisak komandi koje se prepoznaju. Dodavati komande na kraj niza i definisati ponasanje u funkciji izvrsiKomandu
	private final static String[] komande = {"cd", "dir", "mkdir", "deldir", "rnmdir"};
	private static FileStablo fs;
	private Scanner input;
	
	//Konstruktor, prima sistem fajlova
	public CMD(FileStablo fs) {
		input = new Scanner(System.in);
		this.fs = fs;
		this.start();
	}
	
	public static void main(String[] args) {
		SekundarnaMemorija sm = new SekundarnaMemorija(4096);
		FileStablo fs = new FileStablo(sm);
		CMD cmd = new CMD(fs);
		
	}
	
	//Funkcija koja pokrece loop za upis komandi
	public void start() {
		while(input.hasNextLine()) {
			String komanda = input.nextLine();
			if(this.validacijaKomande(komanda)) {
				this.izvrsiKomandu(komanda);
			}
		}
	}
	
	//Funkcija koja prepoznaje da li se komanda nalazi u nizu komandi
	private boolean validacijaKomande(String komanda) {
		String token = komanda.split(" ")[0];
		for(int i=0; i<CMD.komande.length; i++)
			if(token.equals(CMD.komande[i])) {
				return true;
			}
		System.out.println("Invalid command");
		return true;
		
	}
	
	//Funkcija koja definise ponasanje komandi
	private void izvrsiKomandu(String komanda) {
		String[] tokeni = komanda.split(" ");
		//cd
		if(tokeni[0].equals(komande[0]) && tokeni.length == 2) {
			fs.cd(tokeni[1]);
		//dir
		}else if(tokeni[0].equals(komande[1]) && tokeni.length == 1) {
			fs.dir();
		//mkdir
		}else if(tokeni[0].equals(komande[2])&& tokeni.length == 2) {
			fs.mkdir(tokeni[1]);
		//deldir
		}else if(tokeni[0].equals(komande[3]) && tokeni.length == 2) {
			fs.deldir(tokeni[1]);
		//rnmdir
		}else if(tokeni[0].equals(komande[4]) && tokeni.length == 3) {
			fs.rnmdir(tokeni[1], tokeni[2]);
		}/*else if(tokeni[0].equals(komande[n] && tokeni.length == m){
		 ...
		}*/
		
	}
}
