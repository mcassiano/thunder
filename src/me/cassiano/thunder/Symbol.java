package me.cassiano.thunder;

public class Symbol {

    private Token token;
    private String lexeme;

    public Symbol(Token token, String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
    }

    public Symbol(Token token) {
        this.token = token;
        this.lexeme = token.toString();
    }

    public String getLexeme() {
        return lexeme;
    }

    public Token getToken() {
        return token;
    }
}
