package kernel;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import assembler.Assembler;
import fileSystem.FileStablo;
import fileSystem.SekundarnaMemorija;
import proces.Proces;


//Klasa koja predstavlja kernel operativnog sistema
public class Kernel {
	
	//Spisak komandi koje se prepoznaju. Dodavati komande na kraj niza i definisati ponasanje u funkciji izvrsiKomandu
	private final static String[] komande = {"cd", "dir", "mkdir", "deldir", "rnmdir","exe", "exit", "list", "lista"};
	private static FileStablo fs;
	private Scanner input;
	
	//Konstruktor, prima sistem fajlova
	public Kernel(FileStablo fs) {
		input = new Scanner(System.in);
		this.fs = fs;
		this.napraviListuProcesa();
		this.start();
	}
	
	public static void main(String[] args) {
		SekundarnaMemorija sm = new SekundarnaMemorija(4096);
		FileStablo fs = new FileStablo(sm);
		Kernel cmd = new Kernel(fs);
		
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

	public void napraviListuProcesa() {
		String[] list= {"exe pr1.txt","exe pr2.txt res.txt","exe pr3.txt res.txt",
				"exe pr4.txt res.txt","exe pr5.txt res.txt","list"};
        /*
        for(int i=0; i<list.length; i++)
            izvrsiKomandu(list[i]);

         */
		izvrsiKomandu(list[2]);
		izvrsiKomandu(list[4]);
		izvrsiKomandu(list[1]);
		izvrsiKomandu(list[0]);
		izvrsiKomandu(list[2]);
		izvrsiKomandu(list[4]);
		izvrsiKomandu(list[1]);
		izvrsiKomandu(list[1]);
		izvrsiKomandu(list[1]);
		izvrsiKomandu(list[5]);
	}


	//Funkcija koja prepoznaje da li se komanda nalazi u nizu komandi
	private boolean validacijaKomande(String komanda) {
		String token = komanda.split(" ")[0];
		for(int i = 0; i< Kernel.komande.length; i++)
			if(token.equals(Kernel.komande[i])) {
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
		}
		//exe
		else if(tokeni[0].equals(komande[5]) && (tokeni.length == 3 || tokeni.length == 2)) {
			//fajl iz koga ucitavamo asemblerski kod procesa
			File file=new File(tokeni[1]);
			if(file.exists()) {
				ArrayList<String> codeAndData = Assembler.convert(tokeni[1]);
				//System.out.println("Iz Kernela --------------");
				//System.out.println(codeAndData);
				int index=tokeni[1].indexOf('.');
				String name=tokeni[1].substring(0,index)+".asm";
				if(tokeni.length == 3)
					new Proces(codeAndData,name,tokeni[2]);
				else
					new Proces(codeAndData,name,null);
			}else {
				System.out.println("Error! File '"+tokeni[1]+"' does not exist!");
			}
		}
		//exit
		else if(tokeni[0].equals(komande[6]) && tokeni.length == 1)
			exit();

		//list
		else if(tokeni[0].equals(komande[7]) && tokeni.length == 1)
			Proces.list();

			//lista
		else if(tokeni[0].equals(komande[8]) && tokeni.length == 1)
			Proces.lista();

		else
			System.out.println("Error! Invalid parameters!");
		
	}

	public static void exit() {
		System.out.println("Goodbye!");
		System.exit(0);
	}
}
