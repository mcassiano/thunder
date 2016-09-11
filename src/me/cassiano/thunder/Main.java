package me.cassiano.thunder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        String sourceFile = null;
        String outputFile;

        try {
            sourceFile = args[0];
            outputFile = args[1];

        } catch (Exception e) {
            System.out.print("Wrong usage! Try ./lc <inputfile> <outputfile>");
        }

        FileInputStream stream;

        try {
            stream = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            stream = null;
            System.out.print(String.format("Source file (%s) not found!", sourceFile));
        }

        if (stream != null) {

            Symbol sym = LexicalAnalyzer.get().analyze(stream);

            do {
                System.out.println(String.format("Symbol: %s", sym.getToken().name()));
                sym = LexicalAnalyzer.get().analyze(stream);

            }

            while (sym != null);

        }

    }
}
