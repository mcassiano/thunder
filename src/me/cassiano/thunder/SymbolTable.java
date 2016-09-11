package me.cassiano.thunder;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
