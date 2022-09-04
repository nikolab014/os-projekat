package proces;

import java.util.ArrayList;

import memorija.Segment;

//Privremena Proces klasa koja ce se ucitati/izbrisati u/iz memorije. Zamijeniti klasu pravim Procesom.
//Ugledao sam se na "OSprojekat" projekat koji si mi poslala kad sam radio, tj Teodorin(?) jer je i on FCFS.

//Kostur struktura sa neophodnim elementima. Prvo ucitati codeAndData kao Teodora u Assembler.java, pa dobiti segmentTabelu
//pomocu ucitajProces metod Memorije. Sadrzaj codeAndData ce se nalaziti u ArrayList<String> Sadrzaj svih segmenata u tabeli


public class Proces {
	
	private ArrayList<Segment> segmentTabela;
	private ArrayList<String> codeAndData;
	
	public Proces() {
		this.codeAndData = new ArrayList<String>();
		// TODO Auto-generated constructor stub
	}

	public ArrayList<String> getCodeAndData() {
		return codeAndData;
	}
	
	public void setTabela(ArrayList<Segment> tabela) {
		this.segmentTabela = tabela;
	}
	
	public ArrayList<Segment> getTabela(){
		return this.segmentTabela;
	}
	
	public void dodajSegment(Segment s) {
		this.segmentTabela.add(s);
	}

}
