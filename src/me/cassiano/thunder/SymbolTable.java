package me.cassiano.thunder;


import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private static SymbolTable instance;

    private Map<String, Symbol> symbols = new HashMap<String, Symbol>();

    public static SymbolTable get() {

        if (instance == null) {

            instance = new SymbolTable();

            /* init the symbols table with reserved words */

            for (ReservedWord word : ReservedWord.values()) {

                String token = word.name();
                String lexeme = word.toString();

                instance.symbols.put(lexeme, new Symbol(token, lexeme));

            }

        }

        return instance;
    }

    public Symbol getSymbol(String lexeme) {
        return instance.symbols.get(lexeme);
    }

    private SymbolTable() {
    }

}
