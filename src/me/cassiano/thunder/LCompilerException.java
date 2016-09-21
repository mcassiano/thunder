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
 * @version 0.2 19/09/2016
 *
 */

package me.cassiano.thunder;


/* Classe Responsável pelo Tratamento de Excecoes do Compilador */



//class LCompilerException extends Exception {

    // Invalid Character Exception - para token não esperado
    // End Of File Exception - para fim de arquivo não esperado

class UnexpectedEndOfFileException extends Exception{
    UnexpectedEndOfFileException () {
        super("linenumber+\":fim de arquivo não esperado.");
        // linenumber+":fim de arquivo não esperado.";
    }
}

    class EndOfFileException extends Exception{
        EndOfFileException (String message) {
            super(message);
            // linenumber+":fim de arquivo não esperado.";
        }
    }

    class InvalidCharacterException extends Exception{
        InvalidCharacterException () {
            super("");
            // linenumber+":token nao esperado[lexema]";
        }
    }

    class FileNotFoundException extends Exception{
        FileNotFoundException (String message) {
            super(message);
            // "Arquivo nao encontrado.";
        }
    }
//}