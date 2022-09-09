package proces;

import java.util.ArrayList;
import java.util.Queue;

import memorija.Segment;
import memorija.Memorija;

//Proces klasa koja ce se ucitati/izbrisati u/iz memorije.

//Kostur struktura sa neophodnim elementima. Prvo ucitati codeAndData koristeci Assembler.java, pa dobiti segmentTabelu
//pomocu ucitajProces metod Memorije. Sadrzaj codeAndData ce se nalaziti u ArrayList<String> Sadrzaj svih segmenata u tabeli


public class Proces {

	private static ArrayList<Proces> processes = new ArrayList<>();


	private ArrayList<Segment> segmentTabela;
	//private ArrayList<String> codeAndData;
	public ArrayList<String> codeAndData;  //TODO privremeno public

	//brojac procesa
	private static int counter = 0;
	private int pid;
	private String state;
	//private int programCounter;
	private int velicina;
	private String naziv;

	//ime fajla za upis rezultata
	private String file;
	public String trenutniPC;
	public String trenutniR1;
	public String trenutniR2;
	public String trenutniR3;
	public int trenutnid;

	public Proces(ArrayList<String> codeAndData, String name, String file) {
		pid = counter++;
		state = "NEW";
		//programCounter = 0;
		this.codeAndData = codeAndData;
		this.naziv = name;
		this.file = file;
		velicina = codeAndData.size()*16;
		int length=Memorija.powerOfTwo(Memorija.getVelicina());
		//int length= Memorija.powerOfTwo(4096);
		//TODO cemu ovo??
		String firstInstruction="";

		for(int i=0; i<length; i++) {
			firstInstruction+="0";
		}

		trenutniPC = firstInstruction;
		trenutniR1 = "";
		trenutniR2 = "";
		trenutniR3 = "";
		trenutnid = 0;

		processes.add(this);
		this.init();
	}

	//public void init() {
	//	load();
	//}

	//poziva se metod ucitajProces iz Memorije
	public void init() {

		this.segmentTabela = Memorija.ucitajProces(this);

		ProcesScheduler.getReadyQueue().add(this);
		this.setState("READY");

		if(ProcesScheduler.getAktivniProces() == null)
			//poziva se Rasporedjivac procesa koji ce tek pokrenuti Thread za izvrsavanje svakog procesa
			ProcesScheduler.schedule();
	}

	public void exit() {
		this.state = "TERMINATED";

		ProcesScheduler.removeRunningProcess();
		Memorija.oslobodiMemoriju(this);

		//rasporedi novi proces
		ProcesScheduler.schedule();
	}

	//lista aktivnih, tj. ready or running
	public static void lista() {
		Queue<Proces> readyProcesses = ProcesScheduler.getReadyQueue();
		Proces runningProcess = ProcesScheduler.getAktivniProces();
		if(runningProcess == null && readyProcesses.isEmpty())
			System.out.println("There are no processes that are currently in ready or running state.");
		else {
			System.out.println("List of processes:");
			if(runningProcess != null) {
				System.out.println("\tPID: "+runningProcess.pid);
				System.out.println("\tName: "+runningProcess.naziv);
				System.out.println("\tState: "+runningProcess.state);
				System.out.println("\tSize: "+runningProcess.velicina);
			}
			if(!readyProcesses.isEmpty()) {
				for(Proces proces : readyProcesses) {
					System.out.println("\tPID: "+ proces.pid);
					System.out.println("\tName: "+ proces.naziv);
					System.out.println("\tState: "+ proces.state);
					System.out.println("\tSize: "+ proces.velicina);
				}
			}
			System.out.println();
		}
	}

	//izlistavanje svih, ne samo ready i running
	public static void list() {

			System.out.println("List of processes:");
			for(Proces proces : processes) {

					System.out.println("\tPID: " + proces.pid);
					System.out.println("\tName: " + proces.naziv);
					System.out.println("\tState: " + proces.state);
					System.out.println("\tSize: " + proces.velicina);

			}

			System.out.println();

	}

	public int getPid() {
		return pid;
	}

	public String getFile() {
		return file;
	}

	public void setState(String state) {
		this.state=state;
	}

	public int getVelicina(){
		return velicina;
	}

	public String getNaziv(){
		return naziv;
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
