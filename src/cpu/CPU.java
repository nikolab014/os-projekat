package cpu;

import java.io.FileWriter;
import memorija.Memorija;
import memorija.Segment;
import proces.Proces;

public class CPU {
    private static Register IR = new Register();
    public static Register PC = new Register();             //TODO vratiti na private
    public static Register R1 = new Register("R1", "10");
    public static Register R2 = new Register("R2", "11");
    public static Register R3 = new Register("R3", "01");
    private static Proces currentProcess;


    public static void execute(Proces process) {

        System.out.println("Process "+process.getPid()+" started its execution.");
        CPU.currentProcess = process;

        //PCB pprocesa....
        //ovo moze i set sve na 0 jer nije preemptive fcfs
        PC.setValue(process.trenutniPC);
        R1.setValue(process.trenutniR1);
        R2.setValue(process.trenutniR2);
        R3.setValue(process.trenutniR3);

        //System.out.println("~~~~~~~~Executing~~~~~~~~");
        //System.out.println(currentProcess.codeAndData);

        //System.out.println("~~~~~~~~Executing - segmentation table~~~~~~~~");
        //System.out.println(currentProcess.getTabela());

        //ova labela je zbog upotrebe break-a sa labelom da se iskoci iz obje petlje
        labela:
        for(Segment s : currentProcess.getTabela())
            for(String instrukcija : s.getSadrzaj()) {

                //System.out.println("Instrukcija " + instrukcija);
                //System.out.println("PC: "+ PC.getValue());
                IR.setValue(instrukcija);
                PC.increment();
                executeInstruction(IR.getValue());
                //ako smo dosli do kraja koda gdje su naredbe, iza je halt i onda data, pa nam to ne treba
                if(IR.getValue().substring(0,4).equals("0000"))
                    break labela;

            }
    }

    public static void executeInstruction(String instruction) {

        String opCode = instruction.substring(0,4);

            if (opCode.equals("0000")) {
                try {
                    //ovo smo stavili da bismo imali vremena da prikazemo dodavanje novih procesa u readyQueue prije nego sto svi pocetni zavrse sa izvrsavanjem
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("The result of process " + currentProcess.getPid() + ": " + Integer.parseInt(R1.getValue(), 2));
                if (currentProcess.getFile() != null)
                    writeToFile();

                System.out.println("Process " + currentProcess.getPid() + " has completed its execution.");
                System.out.println();
                currentProcess.exit();
            } else if (opCode.equals("0010")) { //LOAD
                String register = instruction.substring(4, 6);
                String memoryLocation = instruction.substring(6);
                int length = Memorija.powerOfTwo(Memorija.getVelicina());
                //int length = Memorija.powerOfTwo(4096);
                String dataLocation = "";
                for (int i = 0; i < length - memoryLocation.length(); i++) {
                    dataLocation += "0";
                }
                dataLocation += memoryLocation;
                //TODO povecati prostor memorije za podatke da nije samo 4 bita....
                //ovo je zapravo dobro u Assembleru i ima veliki raspon brojeva, ovdje u CPU je problem sa dekodiranjem, sto je samo
                //4 bita pa mogu samo mali brojevi, i malo podataka...
                String offsetForData = dataLocation.substring(4, 8);
                String bitoviOffset[] = offsetForData.split("");
                int offset = 0;
                if (Integer.parseInt(bitoviOffset[0]) == 1) offset += 8;
                if (Integer.parseInt(bitoviOffset[1]) == 1) offset += 4;
                if (Integer.parseInt(bitoviOffset[2]) == 1) offset += 2;
                if (Integer.parseInt(bitoviOffset[3]) == 1) offset += 1;


                //System.out.println(offsetForData);
                //System.out.println(offset);
                //System.out.println("Datalocation ~~~~~~~`" + dataLocation);

                //TODO nije iz memorije nego je iz procesa
                String data = currentProcess.codeAndData.get(offset);

                //System.out.println("~~~~~~~~~`data~````````");
                //System.out.println(data);

                if (register.equals(R1.getAddress()))
                    R1.setValue(data);
                else if (register.equals(R2.getAddress()))
                    R2.setValue(data);
                else
                    R3.setValue(data);
            }
            //TODO vidjeti sta cemo za store ali ne treba nam....zasad...

            //ADD    SUB   MUL DIV
            else if (opCode.equals("0100") || opCode.equals("0101") || opCode.equals("0110") || opCode.equals("0111")) {
                String register1 = instruction.substring(8, 10);
                String register2 = instruction.substring(14);
                String data1 = "";
                String data2 = "";

                if (register1.equals(R1.getAddress()))
                    data1 = R1.getValue();
                else if (register1.equals(R2.getAddress()))
                    data1 = R2.getValue();
                else
                    data1 = R3.getValue();

                if (register2.equals(R1.getAddress()))
                    data2 = R1.getValue();
                else if (register2.equals(R2.getAddress()))
                    data2 = R2.getValue();
                else
                    data2 = R3.getValue();

                int dataNumber1 = Integer.parseInt(data1, 2);
                int dataNumber2 = Integer.parseInt(data2, 2);
                int result = 0;

                if (opCode.equals("0100"))
                    result = dataNumber1 + dataNumber2;
                else if (opCode.equals("0101"))
                    result = dataNumber1 - dataNumber2;
                else if (opCode.equals("0110"))
                    result = dataNumber1 * dataNumber2;
                else
                    result = dataNumber1 / dataNumber2;

                String binaryNumber = "";
                if (result == 0)
                    binaryNumber = "0";
                else
                    binaryNumber = Memorija.decToBinary(result);

                if (register1.equals(R1.getAddress()))
                    R1.setValue(binaryNumber);
                else
                    R2.setValue(binaryNumber);
            }

    }

    public static void setToZero() {
        R1.setValue("");
        R2.setValue("");
        R3.setValue("");
        PC.setValue("0");
        IR.setValue("");
    }
    public static void writeToFile(){
        String result=R1.getValue();
        String file=currentProcess.getFile();
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(String.valueOf(Integer.parseInt(result,2)));
            myWriter.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void print() {
        System.out.println("State of memory and registers:");
        System.out.println();
        System.out.println("IR: "+IR.getValue());
        System.out.println("PC: "+PC.getValue());
        System.out.println("R1: "+R1.getValue());
        System.out.println("R2: "+R2.getValue());
        System.out.println("R3: "+R3.getValue());
    }


}