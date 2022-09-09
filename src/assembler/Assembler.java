package assembler;

import cpu.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assembler {

    private static String loadOpCode = "0010";
    private static String storeOpCode = "0011";
    private static String addOpCode = "0100";
    private static String subOpCode = "0101";
    private static String mulOpCode = "0110";
    private static String divOpCode = "0111";

    //koristicemo samo registre R1 i R2 u naredbama u procesima, R3 nam je pomocni registar
    private static Register R1 = new Register("R1","10");
    private static Register R2 = new Register("R2","11");
    private static Register R3 = new Register("R3","01");

    public static ArrayList<String> convert(String filename) {

        ArrayList<String> codeList = new ArrayList<String>();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        HashMap<String,String> nameMap = new HashMap<>();

        int addressCounter = 0;
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {

                String line = myReader.nextLine();
                //preskace prazne linije
                if(line.isEmpty())
                    continue;

                String[] array = line.split(" ");

                if(array.length == 1) {

                    if(array[0].equals("HLT")) {
                        codeList.add("0000000000000000");
                        addressCounter++;
                    }
                }
                else if(array.length == 3) {

                    ArrayList<String> list = new ArrayList<>();

                    if(array[0].equals("ADD") || array[0].equals("SUB") || array[0].equals("MUL") || array[0].equals("DIV")) {
                       //lista od jednog ili dva elementa, ako je npr. ADD R1, 3 onda od dva, a ako je ADD R1, R2 onda od jednog
                        list = operations(array);

                        if(isNumeric(array[2])) {
                            //dodamo u listu numerickih vrijednosti
                            nameMap.put(array[2], "-1");
                            //System.out.println("prvamapa-------");
                            //System.out.println(nameMap);
                            //TODO promeniti da se preskoci ovaj korak

                        }
                    }
                    if(array[0].equals("MOV")) {

                        String tmp = loadOrStore(array);
                        //dodamo u codeList a ne u privremenu list, zato ce se povecati addressCOunter
                        codeList.add(tmp);
                        //dodamo u listu indeksa instrukcija koje poticu od MOV pa su nepotpuno prevedene
                        indexes.add(codeList.indexOf(tmp));
                        if(isNumeric(tmp.substring(6))) {
                            nameMap.put(tmp.substring(6), "-1");
                            //dodamo u listu indeksa instrukcija koje poticu od MOV pa su nepotpuno prevedene
                            //TODO zapravo bi trebalo ovdje da se dodaje a ne iznad, jer je nepotpuna samo ako je numericka vrijednost prisutna
                            //indexes.add(codeList.indexOf(tmp));
                        }
                        addressCounter++;

                    }else {
                        int size = list.size();
                        //povecava se za 1 ili za 2
                        addressCounter += size;

                        //prepisemo jednu ili 2 naredbe u codeList iz list
                        for(int i = 0; i < list.size(); i++) {
                            codeList.add(list.get(i));

                            boolean added = false;
                            //za svako slovo j iz naredbe list.get(i) gledamo da li je razlicito od 0 i 1 (sta ako je 11 npr??)
                            for(int j = 0; j < list.get(i).length(); j++) {
                                if(list.get(i).charAt(j) >'1' || list.get(i).charAt(j) <'0') {
                                    //ako jeste slovo neodgovarajuce, dodajemo indeks naredbe iste kao list.get(i) iz codeList
                                    indexes.add(codeList.indexOf(list.get(i)));
                                    added = true;
                                    break;
                                }
                            }
                            if(added)
                                continue;
                            //ovo je ako nismo vec dodali u listu indexa koji ne odgovaraju
                            //sad gledamo ako duzina te naredbe nije 16, tj ako je broj bio 11 npr. pa ga nismo prepoznali
                            //prethodnom petljom, sad cemo ga dodati svakako u listu index-a
                            if(list.get(i).length() != 16) {
                                indexes.add(codeList.indexOf(list.get(i)));
                            }
                        }
                    }
                }

            }
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

       // System.out.println("Print*********************codeList**********");
        //System.out.println(codeList);
        codeList.add("0000000000000000");  //zalijepi jos jedno 00..00 pored onog poslije HLT
        addressCounter++;

        //zavrsilo se citanje naredbi iz fajla, i prvi upis u codeList, pored koje imamo i nameMap i indexes listu

        //za sve naredbe iz codeList koje nisu "do kraja" u binarnom zapisu
        //prvo cemo njihove "nebinarno zapisane brojeve" dodati u binarnom zapisu u dio podataka
        //na kraj codeLista, a tek onda cemo ispraviti zapis naredbe iz codeList da umjesto numericke vrijednosti
        //sadrzi adresu podataka (koju smo upravo dodali)
        for(int i = 0; i < indexes.size(); i++) {
            int indeks = indexes.get(i);
            //pokupimo u trazenoj naredbi sve poslije 6-tog mjesta, sto ce biti broj (jer je 4 mjesta naredba i 2 registar)
            String pr = codeList.get(indeks).substring(6);
            //pretvorimo taj broj u binarni zapis
            String binaryNumber = decToBinary(Integer.parseInt(pr));

            String number = "";
            //ovdje sad treba podesiti da je sve ukupno zapisano na 16 bita za PODATKE, jer je duzina prethodnog binarnog
            // broja varijabilna -- dodamo ispred onoliko nula koliko fali do 16
            for(int j = 0; j < 16 - binaryNumber.length(); j++)
                number += "0";
            number += binaryNumber;
            codeList.add(number); //dodajemo na kraj broj ----> data

            String num = decToBinary(addressCounter * 16);
            //dobijemo u binarnom zapisu addressCounter, tj adresu gdje pocinju podaci - pa na dalje
            //kako je ta adresa varijabilne duzine, moramo ispred nje dopuniti nulama na 10 cifara, jer je prvih 6 rezervisano
            //vec bilo zapisom naredbe u polovicnom zapisu u codeList (komanda 4 cifre i registar 2)
            String newNumber = "";
            for(int j = 0; j < 10 - num.length(); j++)
                newNumber += "0";
            newNumber += num;
            nameMap.replace(pr,newNumber);  //zamijeniti -1 sa newNumber za broj pr u Stringovskom zapisu u nameMap
            addressCounter++;    //povecamo adresu za sljedeci podatak
            String address = nameMap.get(pr);

            //sad tek mijenjamo instrukciju pocetnu iz codeList, tako sto pokupimo prevedeni prefiks
            String newInstr = codeList.get(indeks).substring(0,6);

            //naredna petlja izgleda nije potrebna, dovoljno je samo:
            //newInstr += newNumber
            //jer je newNumber pa prema tome i address vec podesene duzine od 10 cifara
            for(int j = 0; j < 10 - address.length(); j++)    //dodamo do 10 jer 6 je vec potroseno na opCode i registar
                newInstr += "0";
            newInstr += address;

            //zamenimo u codeList dopola prevedenu instrukciju novom potpuno prevedenom
            codeList.set(indexes.get(i), newInstr);
        }

        //gotovo je generisanje koda, a mozemo da vidimo indexes i nameMap
//        System.out.println("****************");
//        System.out.println(nameMap);
//        System.out.println(indexes);
//        System.out.println("COde list asembler ---> idalje nije binarno svee --> sad jeste");
//        System.out.println(codeList);

        return codeList;
    }

    //vraca listu od jednog ili 2 elementa, u zavisnosti od argumenata
    public static ArrayList<String> operations(String[] array) {

        ArrayList<String> codeList = new ArrayList<String>();
        String tmp = "";

        if(isNumeric(array[2])) {

            String reg = array[1].substring(0, array[1].length() - 1);
            String broj = "";
            broj = array[2];

            //kako je R3 ovdje rezervisan za konstantu, ne pokriva se slucaj npr. ADD R3, 5
            tmp = loadOpCode + "" + R3.getAddress() + "" + broj;
            codeList.add(tmp);
            tmp = "";

            //imamo samo slucajeve npr. ADD R1, 5 i ADD R2, 5
            if(reg.equals("R1")) {

                if(array[0].equals("ADD"))
                    tmp = addOpCode + "0000" + R1.getAddress() + "0000" + R3.getAddress();
                else if(array[0].equals("SUB"))
                    tmp = subOpCode + "0000" + R1.getAddress() + "0000" + R3.getAddress();
                else if(array[0].equals("MUL"))
                    tmp = mulOpCode + "0000" + R1.getAddress() + "0000" + R3.getAddress();
                else
                    tmp = divOpCode + "0000" + R1.getAddress() + "0000" + R3.getAddress();
            }else {
                if(array[0].equals("ADD"))
                    tmp = addOpCode + "0000" + R2.getAddress() + "0000" + R3.getAddress();
                else if(array[0].equals("SUB"))
                    tmp = subOpCode + "0000" + R2.getAddress() + "0000" + R3.getAddress();
                else if(array[0].equals("MUL"))
                    tmp = mulOpCode + "0000" + R2.getAddress() + "0000" + R3.getAddress();
                else
                    tmp = divOpCode + "0000" + R2.getAddress() + "0000" + R3.getAddress();
            }
            codeList.add(tmp);

        }
        //ako nije drugi argument numericki, onda se sabiraju dva registra, mogu i da se izbace slucajebi sa R3 jer je on pomocni
        else {
            String reg1 = array[1];
            String reg2 = array[2];
            //ne pokrivaju se slucajevi kad su dva ista registra, npr. ADD R1, R1
            //ovo je bilo ako zelimo razmatrati slucajeve da neki od registara u naredbi bude i R3:
            //if(reg1.equals("R1,") && reg2.equals("R2")) {
                if (array[0].equals("ADD"))
                    tmp = addOpCode + "0000" + R1.getAddress() + "0000" + R2.getAddress();
                else if (array[0].equals("SUB"))
                    tmp = subOpCode + "0000" + R1.getAddress() + "0000" + R2.getAddress();
                else if (array[0].equals("MUL"))
                    tmp = mulOpCode + "0000" + R1.getAddress() + "0000" + R2.getAddress();
                else
                    tmp = divOpCode + "0000" + R1.getAddress() + "0000" + R2.getAddress();

            codeList.add(tmp);
        }
        return codeList;
    }
    public static String loadOrStore(String[] array) {
        String string = array[2];
        String tmp = "";

        //load je naredba tipa MOV R1, 2
        if(isNumeric(string)) {
            String reg = array[1].substring(0, array[1].length() - 1); //skine se , poslije naziva registra
            if(reg.equals("R1"))
                tmp = loadOpCode + "" + R1.getAddress() + "" + array[2];   //   0010|10|2
            else
                tmp = loadOpCode + "" + R2.getAddress() + "" + array[2];
        }
        //store je naredba tipa MOV R2, R1
        else{
            String reg = array[2];
            if(reg.equals("R1"))
                tmp = storeOpCode + "" + array[1].substring(0, array[1].length()-1) + "" + R1.getAddress();
            else
                tmp = storeOpCode + "" + array[1].substring(0, array[1].length()-1) + "" + R2.getAddress();
        }
        return tmp;
    }

    public static boolean isNumeric(String string) {

        try {
            int intValue = Integer.parseInt(string);
            return true;
        }catch (NumberFormatException e) {

        }
        return false;
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
        // napise unazad u niz, pa ih treba napisati u obrnutom redoslijedu
        for (int j = i - 1; j >= 0; j--)
            binaryNumber += String.valueOf(binaryNum[j]);

        return binaryNumber;
    }

}
