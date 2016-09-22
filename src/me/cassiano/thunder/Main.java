/**
 * ------------------------------------------------------------------
 * Pontifícia Universidade Católica de Minas Gerais
 * Curso de Ciência da Computação
 * Disciplina: Compiladores (2-2016)
 * <p>
 * Trabalho Prático
 * Thunder - Compiler for the fictional Language 'L'
 * <p>
 * Parte 1 - Analisador Léxico e Analisador Sintático
 * <p>
 * Objetivo:
 * Construção de um compilador que traduza programas na linguagem fonte "L"
 * para um subconjunto do ASSEMBLY da família 80x86.
 *
 * @author Ana Cristina Pereira Teixeira    Matrícula: 427385
 * @author Mateus Loures do Nascimento      Matricula: 511709
 * @author Matheus Cassiano Cândido         Matricula: 454481
 * @version 0.1 11/09/2016
 * @version 0.2 19/09/2016
 */

package me.cassiano.thunder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/* Programa Principal */

public class Main {

    public static void main(String[] args) {

        String sourceFile = null;
        String outputFile;

        try {
            sourceFile = args[0];
            outputFile = args[1];

        } catch (Exception e) {
            System.out.print("Wrong usage! Try ./lc <inputfile> <outputfile>");
            return;
        }

        PushbackInputStream stream;

        try {
            InputStream is = new FileInputStream(sourceFile);
            stream = new PushbackInputStream(is);
        } catch (FileNotFoundException e) {
            System.out.print(String.format("Source file (%s) not found!", sourceFile));
            return;
        }

        try {
            Parser p = new Parser(stream);
            p.start();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (UnexpectedEndOfFileException | UnexpectedToken | UnknownLexeme e) {
            System.out.println(e.getMessage());

        }

    }
}
