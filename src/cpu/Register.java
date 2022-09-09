package cpu;

import memorija.Memorija;

public class Register {
    private String value;
    private String address;
    private String name;

    public Register(String name, String address) {
        this.address = address;
        this.name = name;
    }
    public Register() {}

    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public String getAddress() {
        return address;
    }

    public void increment() {
        int number = Integer.parseInt(value,2);
        number++;
        String binaryNumber = Memorija.decToBinary(number+16);
        String newBinary = "";
        for(int i = 0; i < Memorija.powerOfTwo(Memorija.getVelicina()) - binaryNumber.length(); i++)
            newBinary += "0";
        newBinary += binaryNumber;
        value = newBinary;
    }

}
