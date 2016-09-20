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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* Classe Responsável pela Manipulação da Tabela de Símbolos */

public class SymbolTable {

    private static SymbolTable instance;

    private Map<String, Symbol> symbols = new HashMap<String, Symbol>();

    public static SymbolTable get() {

        if (instance == null) {

            instance = new SymbolTable();

            /* init the symbols table with reserved words */

            for (Token token : Token.values()) {

                String lexeme = token.toString();
                instance.symbols.put(lexeme, new Symbol(token, lexeme));

            }

        }

        return instance;
    }

    public boolean hasSymbol(String lexeme) {
        return instance.symbols.containsKey(lexeme);
    }

    public Symbol getSymbol(String lexeme) {
        return instance.symbols.get(lexeme);
    }

    public void putSymbol(Symbol sym) {
        instance.symbols.put(sym.getLexeme(), sym);
    }

    public Set<Map.Entry<String, Symbol>> symbols() {
        return instance.symbols.entrySet();
    }

    private SymbolTable() {
    }

}
