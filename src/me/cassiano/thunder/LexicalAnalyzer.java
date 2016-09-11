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
import java.io.IOException;

/* Classe responsável pela Análise Léxica */

public class LexicalAnalyzer {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_WIN = "\r";
    private static final String TAB = "\t";

    private static final LexicalAnalyzer instance = new LexicalAnalyzer();

    public static LexicalAnalyzer get() {
        return instance;
    }

    public Symbol analyze(FileInputStream fileStream) {

        State state = State.Q_START;
        String lexeme = "";
        Symbol sym = null;

        while (state != State.Q_END) {

            String charRead = readChar(fileStream);

            if (charRead == null)
                return null;

            if (shouldIgnore(charRead))
                continue;

            lexeme += charRead;

            if (SymbolTable.get().hasSymbol(lexeme)) {

                sym = SymbolTable.get().getSymbol(lexeme);

                switch (sym.getToken()) {

                    case EQUALS:
                    case LEFT_PARENTHESIS:
                    case RIGHT_PARENTHESIS:
                    case APOSTROPHE:
                    case PLUS:
                    case MINUS:
                    case MULTIPLY:
                    case SEMICOLON:
                    case UNDERSCORE:
                        state = State.Q_END;
                        break;
                }
            }
        }


        return sym;
    }

    private String readChar(FileInputStream fileStream) {

        int _char;
        String tChar;

        try {
            _char = fileStream.read();

            if (_char == -1)
                throw new IOException("EOF");

            tChar = String.valueOf((char) _char);
        } catch (IOException e) {
            tChar = null;
        }

        return tChar;
    }

    private boolean shouldIgnore(String str) {

        return  str.isEmpty() ||
                str.equals(NEW_LINE) ||
                str.equals(NEW_LINE_WIN) ||
                str.equals(TAB) ||
                str.equals(BLANK);
    }

    private enum State {

        Q_START,
        Q_END
    }

}
