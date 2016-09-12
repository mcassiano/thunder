package me.cassiano.thunder;


public enum Token {

    ID("[A-Za-z_][A-Za-z_0-9]*"),
    FINAL("final"),
    INT("int"),
    BYTE("byte"),
    STRING("string"),
    WHILE("while"),
    IF("if"),
    ELSE("else"),
    AND("&&"),
    OR("||"),
    NOT("!"),
    ATTRIBUTION("<-"),
    EQUALS("="),
    RIGHT_PARENTHESIS(")"),
    LEFT_PARENTHESIS("("),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    NOT_EQUALS("!="),
    GREATER_THAN_EQUALS(">="),
    LESS_THAN_EQUALS("<="),
    COMMA(","),
    PLUS("+"),
    MINUS("-"),
    ASTERISK("*"),
    FORWARD_SLASH("/"),
    SEMICOLON(";"),
    BEGIN("begin"),
    END_WHILE("endwhile"),
    END_IF("endif"),
    END_ELSE("endelse"),
    READ_LINE("readln"),
    WRITE("write"),
    WRITE_LINE("writeln"),
    TRUE("true"),
    FALSE("false"),
    APOSTROPHE("'"),
    BOOLEAN("boolean"),
    EOF("eof"),
    UNDERSCORE("_");

    private final String text;

    /**
     * @param text
     * lexeme for the symbol
     */
    Token(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    public static Token fromString(String text) {

        if (text != null) {
            for (Token b : Token.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}