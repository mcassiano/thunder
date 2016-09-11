/**
 * ------------------------------------------------------------------
 * Pontifícia Universidade Católica de Minas Gerais
 * Curso de Ciência da Computação
 * Disciplina: Compiladores (2-2016)
 *
 * Trabalho Prático
 * Thunder - Compiler for the fictional Language 'L'
 *
 * Parte 1 - Analisador Léxico e Analisador Sintático
 *
 * Objetivo:
 * Construção de um compilador que traduza programas na linguagem fonte "L"
 * para um subconjunto do ASSEMBLY da família 80x86.
 *
 *
 * @author Ana Cristina Pereira Teixeira    Matrícula: 427385
 * @author Mateus Loures do Nascimento      Matricula: 511709
 * @author Matheus Cassiano Cândido         Matricula: 454481
 * @version 0.1 11/09/2016
 *
 */

package me.cassiano.thunder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
