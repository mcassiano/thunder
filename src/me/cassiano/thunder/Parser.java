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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

import static me.cassiano.thunder.Token.*;


/* Classe Responsável pela Análise Sintática */

public class Parser {

    //private LexicalAnalyzer lexAn;
    private Symbol currentToken;
    //private String lexema;
    private PushbackInputStream fileStream;
    public static long linenumber;

    public Parser(PushbackInputStream fileStream) throws IOException {
        this.fileStream=fileStream;
        this.currentToken = LexicalAnalyzer.get().analyze(fileStream); //le o primeiro token
    }

    public void casaToken (Token tokenrecebido) throws IOException {

        if(currentToken.getToken().equals(tokenrecebido)){
            System.out.println("lexema: "+currentToken.getLexeme()+" token: "+currentToken.getToken().name());
            currentToken = LexicalAnalyzer.get().analyze(fileStream);
        }else if (currentToken.getToken().equals(Token.EOF))
            System.out.println("ERRO - EOF");
        else
            System.out.println("ERRO - lex  '"+currentToken.getLexeme()+"' nao esperado");
    }

    public void run(PushbackInputStream fileStream) throws IOException {
        this.currentToken = LexicalAnalyzer.get().analyze(fileStream);
    }

    public void start() throws IOException {
        // colocar ambos na repeticao
        declaration();
        casaToken(SEMICOLON);
        commands();
    }

    public void imprimeToken(Token token) {
        System.out.println(token.toString());
    }

    public void declaration() throws IOException {

        switch (currentToken.getToken()) {
            case FINAL:
                casaToken(FINAL);
                break;
            case INT:
                casaToken(INT);
                break;
            case BOOLEAN:
                casaToken(BOOLEAN);
                break;
            default:
                casaToken(STRING);
                break;
        }

        casaToken(ID);

        if (currentToken.getToken().equals(Token.ATTRIBUTION)) {
            casaToken(ATTRIBUTION);
            casaToken(CONSTANT);
        }

        while (currentToken.getToken().equals(Token.COMMA)) {
            casaToken(COMMA);
            casaToken(ID);

            if (currentToken.getToken().equals(Token.ATTRIBUTION)) {
                casaToken(ATTRIBUTION);
                casaToken(CONSTANT);
            }

        }
    }

    public void commands() throws IOException {

        switch (currentToken.getToken()) {
            case ID:
                casaToken(ID);
                casaToken(ATTRIBUTION);
                expression();
                casaToken(SEMICOLON);
                break;

            case READ_LINE:
                casaToken(READ_LINE);
                casaToken(LEFT_PARENTHESIS);
                casaToken(ID);
                casaToken(RIGHT_PARENTHESIS);
                casaToken(SEMICOLON);
                break;

            case WRITE:
            case WRITE_LINE:

                if (currentToken.getToken() == WRITE) {
                    casaToken(WRITE);
                } else casaToken(WRITE_LINE);

                casaToken(LEFT_PARENTHESIS);

                expression();

                while (currentToken.getToken() == COMMA) {
                    casaToken(COMMA);
                    expression();
                }

                casaToken(RIGHT_PARENTHESIS);
                casaToken(SEMICOLON);
                break;

            case WHILE:
                casaToken(WHILE);
                casaToken(LEFT_PARENTHESIS);
                expression();
                casaToken(RIGHT_PARENTHESIS);

                if (currentToken.getToken() == BEGIN) {
                    casaToken(BEGIN);

                    while (currentToken.getToken() != END_WHILE) {
                        commands();
                        casaToken(SEMICOLON);
                    }
                    casaToken(END_WHILE);
                } else {
                    commands();
                    casaToken(SEMICOLON);
                }
                break;
        }
    }

    public void logic_operators () throws IOException {
        switch (currentToken.getToken()) {
            case LESS_THAN:
                casaToken(LESS_THAN);
                break;
            case GREATER_THAN:
                casaToken(GREATER_THAN);
                break;
            case LESS_THAN_EQUALS:
                casaToken(LESS_THAN_EQUALS);
                break;
            case GREATER_THAN_EQUALS:
                casaToken(GREATER_THAN_EQUALS);
                break;
            case NOT_EQUALS:
                casaToken(NOT_EQUALS);
                break;
            default:
                casaToken(EQUALS);
                break;
        }
    }

    public void expression() throws IOException {

        exp_sum();

        if (currentToken.getToken() == LESS_THAN ||
                currentToken.getToken() == GREATER_THAN ||
                currentToken.getToken() == LESS_THAN_EQUALS ||
                currentToken.getToken() == GREATER_THAN_EQUALS ||
                currentToken.getToken() == NOT_EQUALS ||
                currentToken.getToken() == EQUALS)
        {
            logic_operators(); // casa token está dentro desse metodo

            exp_sum();

        }
    }

    public void exp_sum() throws IOException {

        if (currentToken.getToken() == PLUS)
            casaToken(PLUS);
        else if (currentToken.getToken() == MINUS)
            casaToken(MINUS);

        exp_product();

        while ( currentToken.getToken() == PLUS ||
                currentToken.getToken() == MINUS ||
                currentToken.getToken() == OR )
        {

            switch (currentToken.getToken()) {
                case PLUS:
                    casaToken(PLUS);
                    break;
                case MINUS:
                    casaToken(MINUS);
                    break;
                default:
                    casaToken(OR);
                    break;
            }

            exp_product();
        }
    }

    public void exp_product() throws IOException {
        exp_value();
        while ( currentToken.getToken() == ASTERISK ||
                currentToken.getToken() == FORWARD_SLASH ||
                currentToken.getToken() == AND)
        {
            switch (currentToken.getToken()){
                case ASTERISK:
                    casaToken(ASTERISK);
                    break;
                case FORWARD_SLASH:
                    casaToken(FORWARD_SLASH);
                    break;
                default:
                    casaToken(AND);
                    break;
            }

            exp_value();
        }
    }

    public void exp_value() throws IOException {
        switch (currentToken.getToken()){
            case LEFT_PARENTHESIS:
                casaToken(LEFT_PARENTHESIS);
                expression();
                casaToken(RIGHT_PARENTHESIS);
                break;
            case ID:
                casaToken(ID);
                break;
            case NOT:
                casaToken(NOT);
                exp_value();
                break;
            case STRING_LITERAL:
                casaToken(STRING_LITERAL);
                break;
            case CONSTANT_HEX:
                casaToken(CONSTANT_HEX);
                break;
            default:
                casaToken(CONSTANT);
                break;

        }

    }

}
