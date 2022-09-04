package fileSystem;

//Klasa koja predstavlja jedan blok, tj. cjelinu memorije koja sluzi za dodjeljivanje datotekama.

public class Blok {

	private final static int VELICINA=4;
	private final int adresa;
	
	public Blok(int adresa) {
		this.adresa = adresa;
	}

	public int getAdresa() {
		return adresa;
	}

	public static int getVelicina() {
		return VELICINA;
	}

}
