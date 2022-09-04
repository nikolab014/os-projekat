package fileSystem;

//Klasa koja predstavlja drvoliki fajl sistem
public class FileStablo {
	
	private Cvor root;
	private Cvor trenutniCvor;
	private SekundarnaMemorija sm;
	
	//Konstruktor koji inicijalizuje fajl sistem, prima sekundarnu memoriju. Baca OutOfMemoryError ako nema dovoljno memorije za root(nikad).
	public FileStablo(SekundarnaMemorija sekmem) throws OutOfMemoryError {
		this.sm = sekmem;
		this.root = new Cvor("root", null, sm);
		this.trenutniCvor = this.root;
		System.out.println(root.toString());
	}
	
	/* Funkcija za mijenjanje aktuelnog direktorijuma. Prelazi na poddirektorijum trenutnog direktorijuma ciji se naziv proslijedi kao dest,
	 * ili u roditeljski direktorijum ako se proslijedi ".." 
	 */
	public void cd(String dest) {
		if(dest.equals("..") && this.trenutniCvor.getRoditelj() != null) {
			this.trenutniCvor = this.trenutniCvor.getRoditelj();
		}else if(!dest.equals("..")){
			for(Cvor c: trenutniCvor.getPotomci())
				if(c.isDir() && c.getNaziv().equals(dest))
					this.trenutniCvor = c;
		}
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Funkcija za ispis svih fajlova i poddirektorijuma u trenutnom direktorijumu. 
	public void dir() {
		System.out.println(this.trenutniCvor.toString() + " :");
		for(Cvor c: this.trenutniCvor.getPotomci()) {
			if(c.isDir())
				System.out.print("Folder: ");
			else
				System.out.print("File: ");
			System.out.print(c.getNaziv()+"\n");
		}
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Funkcija za kreiranje novog poddirektorijuma u trenutnom direktorijumu
	public void mkdir(String naziv) {
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv)) {
				System.out.println("Unable to mkdir: Folder already exists");
				System.out.println(this.trenutniCvor.toString());
				return;
			}
		Cvor tempCvor;
		try {
			tempCvor = new Cvor(naziv, this.trenutniCvor, this.sm);
			this.trenutniCvor.getPotomci().add(tempCvor);
		} catch (OutOfMemoryError e) {
			System.out.println("Unable to mkdir: Insufficient memory");
		}
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Funkcija za brisanje poddirektorijuma trenutnog direktorijuma.
	public synchronized void deldir(String naziv) {
		Cvor cZaUkloniti = null; 
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv))
				if(!c.isDir()) {
					System.out.println("Unable to deldir: Is not a folder");
					System.out.println(this.trenutniCvor.toString());
					return;
				}else {
					cZaUkloniti = c;
					break;
				}
		if(cZaUkloniti != null) {
			this.deldirOslobodiMemoriju(cZaUkloniti);
			trenutniCvor.getPotomci().remove(cZaUkloniti);
		}
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Pomocna funkcija za deldir koja oslobadja memoriju
	private void deldirOslobodiMemoriju(Cvor c) {
		this.sm.brisanjeDatoteke(c.getDatoteka());
		for(Cvor c1: c.getPotomci())
			this.deldirOslobodiMemoriju(c1);
	}
	
	//Funkcija za preimenovanje poddirektorijuma
	public void rnmdir(String naziv, String noviNaziv) {
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv)) {
				if(!c.isDir()) {
					System.out.println("Unable to rnmdir: Is not a folder.");
					System.out.println(this.trenutniCvor.toString());
					return;
				}
				c.setNaziv(noviNaziv);
				System.out.println(this.trenutniCvor.toString());
				return;
			}
		System.out.println("Unable to rnmdir: File not found.");
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Funkcija za kreiranje novog fajla u trenutnom direktorijumu sa odredjenim sadrzajem
	public void mkfile(String naziv, String sadrzaj) {
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv)) {
				System.out.println("Unable to mkfile: File already exists");
				System.out.println(this.trenutniCvor.toString());
				return;
			}
		Cvor tempCvor;
		try {
			tempCvor = new Cvor(naziv, sadrzaj, this.trenutniCvor, this.sm);
			this.trenutniCvor.getPotomci().add(tempCvor);
		} catch (OutOfMemoryError e) {
			System.out.println("Unable to mkfile: Insufficient memory");
		}
		System.out.println(this.trenutniCvor.toString());
	}
	
	//Funkcija koja vraca sadrzaj fajla u trenutnom direktorijumu, null ako ne postoji ili ako je folder
	public String rdfile(String naziv) {
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv))
				if(c.isDir()) {
					System.out.println("Unable to rdfile: Is not a file");
					System.out.println(this.trenutniCvor.toString());
					return null;
				}else {
					System.out.println(this.trenutniCvor.toString());
					return this.sm.sadrzajDatoteke(c.getDatoteka());
				}
		System.out.println("Unable to rdfile: File not found");
		System.out.println(this.trenutniCvor.toString());
		return null;
	}
	
	//Funkcija za brisanje fajla u trenutnom direktorijumu.
	public synchronized void delfile(String naziv) {
		Cvor cZaUkloniti = null;
		for(Cvor c: this.trenutniCvor.getPotomci())
			if(c.getNaziv().equals(naziv))
				if(c.isDir()) {
					System.out.println("Unable to delfile: Is not a file");
					System.out.println(this.trenutniCvor.toString());
					return;
				}else {
					cZaUkloniti = c;
					break;
				}
		if(cZaUkloniti != null) {
			this.sm.brisanjeDatoteke(cZaUkloniti.getDatoteka());
			this.trenutniCvor.getPotomci().remove(cZaUkloniti);
			System.out.println(this.trenutniCvor.toString());
			return;
		}
		System.out.println("Unable to delfile: File not found");
		System.out.println(this.trenutniCvor.toString());
	}
	
}

















