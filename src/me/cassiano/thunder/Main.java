package me.cassiano.thunder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.cassiano.thunder.exception.UnexpectedEndOfFile;

public class Main {

    public static void main(String[] args) throws IOException, UnexpectedEndOfFile {

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

            Symbol sym;

            while ((sym = LexicalAnalyzer.get().analyze(stream)).getToken() != Token.EOF) {
                System.out.println(String.format("TOKEN: %s. LEXEME: %s",
                        sym.getToken().name(), sym.getLexeme()));
            }


        }

    }
}
