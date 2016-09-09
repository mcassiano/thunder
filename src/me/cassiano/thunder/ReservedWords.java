package me.cassiano.thunder;


public enum ReservedWords {

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
    MULTIPLY("*"),
    DIVIDE("/"),
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
    BOOLEAN("boolean");

    private final String text;

    /**
     * @param text
     * lexeme for the symbol
     */
    ReservedWords(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}