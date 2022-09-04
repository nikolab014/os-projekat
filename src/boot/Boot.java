package boot;

import fileSystem.FileStablo;
import fileSystem.SekundarnaMemorija;
import komandnaLinija.CMD;

//Klasa za pokretanje svih elemenata OS-a

//Note - valjda je to to? Nista drugo mi ne pada na pamet 
public class Boot {

	public static void main(String[] args) {
		boot();
	}
	
	//TODO
	public static void boot() {
		SekundarnaMemorija sm = new SekundarnaMemorija(4096);
		FileStablo fs = new FileStablo(sm);
		CMD cmd = new CMD(fs);
	}

}
