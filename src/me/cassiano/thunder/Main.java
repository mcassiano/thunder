package me.cassiano.thunder;

public class Main {

    public static void main(String[] args) {

        String sourceFile;
        String outputFile;

        try {
            sourceFile = args[0];
            outputFile = args[1];

        } catch (Exception e) {
            System.out.print("Wrong usage! Try ./lc <inputfile> <outputfile>");
        }

    }
}
