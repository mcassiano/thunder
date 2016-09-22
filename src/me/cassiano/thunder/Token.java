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

/* Classe Responsável pela Definição dos Tokens */

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
    //APOSTROPHE("'"),
    BOOLEAN("boolean"),
    EOF(""), // NAO EXISTE NA TABELA
    UNDERSCORE("_"),
    STRING_LITERAL("[0-9A-Za-z]"),
    CONSTANT("[0-9]"),
    CONSTANT_HEX("[0-9A-F]");


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
                if (text.equals(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}