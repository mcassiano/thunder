package me.cassiano.thunder;

public class Symbol {

    private String token;
    private String lexeme;

    public Symbol(String token, String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getToken() {
        return token;
    }
}
