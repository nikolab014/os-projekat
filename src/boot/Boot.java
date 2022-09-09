package boot;

import fileSystem.FileStablo;
import fileSystem.SekundarnaMemorija;
import kernel.Kernel;

//Klasa za pokretanje svih elemenata OS-a

public class Boot {

	public static void main(String[] args) {
		boot();
	}

	public static void boot() {
		SekundarnaMemorija sm = new SekundarnaMemorija(4096);
		FileStablo fs = new FileStablo(sm);
		Kernel kernel = new Kernel(fs);
	}

}
