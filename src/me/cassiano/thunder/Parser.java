/**
 * ------------------------------------------------------------------
 * Pontifícia Universidade Católica de Minas Gerais
 * Curso de Ciência da Computação
 * Disciplina: Compiladores (2-2016)
 * <p>
 * Trabalho Prático
 * Thunder - Compiler for the fictional Language 'L'
 * <p>
 * Parte 1 - Analisador Léxico e Analisador Sintático
 * <p>
 * Objetivo:
 * Construção de um compilador que traduza programas na linguagem fonte "L"
 * para um subconjunto do ASSEMBLY da família 80x86.
 *
 * @author Ana Cristina Pereira Teixeira    Matrícula: 427385
 * @author Mateus Loures do Nascimento      Matricula: 511709
 * @author Matheus Cassiano Cândido         Matricula: 454481
 * @version 0.1 11/09/2016
 * @version 0.2 19/09/2016
 */

package me.cassiano.thunder;

import java.io.IOException;
import java.io.PushbackInputStream;

import static me.cassiano.thunder.Token.AND;
import static me.cassiano.thunder.Token.ASTERISK;
import static me.cassiano.thunder.Token.ATTRIBUTION;
import static me.cassiano.thunder.Token.BEGIN;
import static me.cassiano.thunder.Token.BOOLEAN;
import static me.cassiano.thunder.Token.BYTE;
import static me.cassiano.thunder.Token.COMMA;
import static me.cassiano.thunder.Token.CONSTANT;
import static me.cassiano.thunder.Token.ELSE;
import static me.cassiano.thunder.Token.END_ELSE;
import static me.cassiano.thunder.Token.END_IF;
import static me.cassiano.thunder.Token.END_WHILE;
import static me.cassiano.thunder.Token.EOF;
import static me.cassiano.thunder.Token.EQUALS;
import static me.cassiano.thunder.Token.FALSE;
import static me.cassiano.thunder.Token.FINAL;
import static me.cassiano.thunder.Token.FORWARD_SLASH;
import static me.cassiano.thunder.Token.GREATER_THAN;
import static me.cassiano.thunder.Token.GREATER_THAN_EQUALS;
import static me.cassiano.thunder.Token.ID;
import static me.cassiano.thunder.Token.IF;
import static me.cassiano.thunder.Token.INT;
import static me.cassiano.thunder.Token.LEFT_PARENTHESIS;
import static me.cassiano.thunder.Token.LESS_THAN;
import static me.cassiano.thunder.Token.LESS_THAN_EQUALS;
import static me.cassiano.thunder.Token.MINUS;
import static me.cassiano.thunder.Token.NOT;
import static me.cassiano.thunder.Token.NOT_EQUALS;
import static me.cassiano.thunder.Token.OR;
import static me.cassiano.thunder.Token.PLUS;
import static me.cassiano.thunder.Token.READ_LINE;
import static me.cassiano.thunder.Token.RIGHT_PARENTHESIS;
import static me.cassiano.thunder.Token.SEMICOLON;
import static me.cassiano.thunder.Token.STRING;
import static me.cassiano.thunder.Token.TRUE;
import static me.cassiano.thunder.Token.WHILE;
import static me.cassiano.thunder.Token.WRITE;
import static me.cassiano.thunder.Token.WRITE_LINE;


/* Classe Responsável pela Análise Sintática */

public class Parser {

    private Symbol currentToken;
    private PushbackInputStream fileStream;

    Memoria memoria;
    Rotulo rotulo;
    Buffer buf;
    int endereco = memoria.contador;

    int F_end = 0;
    int T_end = 0;
    int Exps_end = 0;
    int Exp_end = 0;

    public Parser(PushbackInputStream fileStream) throws IOException {
        this.fileStream = fileStream;
        memoria = new Memoria();
        rotulo = new Rotulo();
        buf = new Buffer();
    }

    public void casaToken(Token tokenrecebido) throws IOException, UnexpectedEndOfFileException, UnexpectedToken, InvalidCharacterException, UnknownLexeme {


        if (currentToken != null && currentToken.getToken().equals(tokenrecebido)) {

            String messageFormat = "Token: %s, Lexeme: %s";
            String message = String.format(messageFormat, currentToken.getToken().name(),
                    currentToken.getLexeme());

            System.out.println(message);

            currentToken = LexicalAnalyzer.get().analyze(fileStream);
        } else if (currentToken == null)
            throw new UnexpectedEndOfFileException(LexicalAnalyzer.get().getLineNumber());
        else
            throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(), currentToken.getToken());
    }

    public void start() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes, IncompatibleIdentifierClass, IdentifierInUse {


        //cabeçalho

        buf.buffer.add("sseg SEGMENT STACK ;início seg. pilha");
        buf.buffer.add("byte 4000h DUP(?) ;dimensiona pilha");
        buf.buffer.add("sseg ENDS ;fim seg. pilha");
        buf.buffer.add("dseg SEGMENT PUBLIC ;início seg. dados");
        buf.buffer.add("byte 4000h DUP(?) ;temporários");
        endereco = memoria.alocarTemp();

        this.currentToken = LexicalAnalyzer.get().analyze(fileStream); //le o primeiro token

        while (declaration()) {
            casaToken(SEMICOLON);
        }

        buf.buffer.add("dseg ENDS ;fim seg. dados");
        buf.buffer.add("cseg SEGMENT PUBLIC ;início seg. código");
        buf.buffer.add("ASSUME CS:cseg, DS:dseg");
        buf.buffer.add("strt:");
        buf.buffer.add("mov ax, dseg");
        buf.buffer.add("mov ds, ax");

        do {
            commands();
        } while (fileStream.available() != 0);

        buf.buffer.add("mov ah, 4Ch");
        buf.buffer.add("int 21h");
        buf.buffer.add("cseg ENDS ;fim seg. código");
        buf.buffer.add("END strt ;fim programa");

        buf.criarArquivo();

    }


    public boolean declaration() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes, IdentifierInUse {

        if (currentToken == null)
            return false;

        if (currentToken.getToken() == FINAL) {

            casaToken(FINAL);
            Symbol tempID = currentToken;

            if (tempID.getClass_() != null)
                throw new IdentifierInUse(LexicalAnalyzer.get().getLineNumber(), tempID.getLexeme());

            tempID.setClass_(SymbolClass.CONST);
            casaToken(ID);
            casaToken(ATTRIBUTION);
            SymbolType tempType = exp_value_const();

            tempID.setType(tempType);

        } else {
            SymbolType tempType;

            switch (currentToken.getToken()) {
                case INT:
                    tempType = SymbolType.INTEGER;
                    casaToken(INT);
                    break;
                case BOOLEAN:
                    tempType = SymbolType.LOGICAL;
                    casaToken(BOOLEAN);
                    break;
                case STRING:
                    tempType = SymbolType.STRING;
                    casaToken(STRING);
                    break;
                case BYTE:
                    tempType = SymbolType.BYTE;
                    casaToken(BYTE);
                    break;
                default:
                    return false;
            }

            if (currentToken.getClass_() != null)
                throw new IdentifierInUse(LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());

            Symbol tempId = currentToken;
            tempId.setClass_(SymbolClass.VAR);
            tempId.setType(tempType);

            casaToken(ID);

            if (currentToken.getToken().equals(Token.ATTRIBUTION)) {
                casaToken(ATTRIBUTION);
                SymbolType type = exp_value_const();

                if (type != tempType)
                    if (!(tempType == SymbolType.INTEGER && type == SymbolType.BYTE))
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString(), type.toString());
            }

            while (currentToken.getToken().equals(Token.COMMA)) {
                casaToken(COMMA);

                if (currentToken.getClass_() != null)
                    throw new IdentifierInUse(LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());

                tempId = currentToken;
                tempId.setClass_(SymbolClass.VAR);
                tempId.setType(tempType);

                casaToken(ID);

                if (currentToken.getToken().equals(Token.ATTRIBUTION)) {

                    casaToken(ATTRIBUTION);
                    SymbolType type = exp_value_const();

                    if (type != tempType)
                        if (!(tempType == SymbolType.INTEGER && type == SymbolType.BYTE))
                            throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString(), type.toString());
                }
            }
        }
        return true;
    }

    public void commands() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes, IncompatibleIdentifierClass {

        if (currentToken == null)
            return;

        SymbolType tempType;

        switch (currentToken.getToken()) {
            case ID:

                Symbol id = currentToken;

                if (id.getClass_() == null)
                    throw new UnknownIdentifier(
                            LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());

                else if (id.getClass_() == SymbolClass.CONST)
                    throw new IncompatibleIdentifierClass(
                            LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());

                casaToken(ID);
                casaToken(ATTRIBUTION);

                tempType = expression();


                if (id.getType() != tempType)
                    if (!(id.getType() == SymbolType.INTEGER && tempType == SymbolType.BYTE))
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), id.getType().toString(), tempType.toString());


                casaToken(SEMICOLON);
                break;

            case READ_LINE:
                casaToken(READ_LINE);
                casaToken(LEFT_PARENTHESIS);

                Symbol tempId = currentToken;
                casaToken(ID);

                if (tempId.getClass_() == null)
                    throw new UnknownIdentifier(LexicalAnalyzer.get().getLineNumber(), tempId.getLexeme());

                else if (tempId.getClass_() == SymbolClass.CONST)
                    throw new IncompatibleIdentifierClass(LexicalAnalyzer.get().getLineNumber(), tempId.getLexeme());

                else if (tempId.getType() == SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempId.getLexeme());

                casaToken(RIGHT_PARENTHESIS);
                casaToken(SEMICOLON);
                break;

            case WRITE:
            case WRITE_LINE:

                if (currentToken.getToken() == WRITE) {
                    casaToken(WRITE);
                } else casaToken(WRITE_LINE);

                casaToken(LEFT_PARENTHESIS);

                tempType = expression();

                if (tempType == SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString());

                while (currentToken.getToken() == COMMA) {
                    casaToken(COMMA);
                    tempType = expression();

                    if (tempType == SymbolType.LOGICAL)
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString());
                }

                casaToken(RIGHT_PARENTHESIS);
                casaToken(SEMICOLON);
                break;

            case WHILE:
                casaToken(WHILE);
                casaToken(LEFT_PARENTHESIS);
                tempType = expression();

                if (tempType != SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString());

                casaToken(RIGHT_PARENTHESIS);

                if (currentToken.getToken() == BEGIN) {
                    casaToken(BEGIN);

                    while (currentToken.getToken() != END_WHILE) {
                        commands();
                    }
                    casaToken(END_WHILE);
                } else {
                    commands();
                }
                break;
            case IF:
                casaToken(IF);
                casaToken(LEFT_PARENTHESIS);
                tempType = expression();

                if (tempType != SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString());

                casaToken(RIGHT_PARENTHESIS);

                if (currentToken.getToken() == BEGIN) {
                    casaToken(BEGIN);

                    while (currentToken.getToken() != END_IF) {
                        commands();
                    }
                    casaToken(END_IF);

                } else {
                    commands();
                }

                if (currentToken.getToken() == ELSE) {
                    casaToken(ELSE);

                    if (currentToken.getToken() == BEGIN) {
                        casaToken(BEGIN);

                        while (currentToken.getToken() != END_ELSE) {
                            commands();
                        }
                        casaToken(END_ELSE);
                    } else {
                        commands();
                    }
                }

                break;

            default:
                if (currentToken.getToken() == EOF)
                    throw new UnexpectedEndOfFileException(LexicalAnalyzer.get().getLineNumber());

                throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(), currentToken.getToken());
        }
    }

    public void logic_operators() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, InvalidCharacterException, UnknownLexeme {
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

    public SymbolType expression() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        SymbolType expType = exp_sum();

        if (currentToken == null)
            return null;

        if (currentToken.getToken() == LESS_THAN ||
                currentToken.getToken() == GREATER_THAN ||
                currentToken.getToken() == LESS_THAN_EQUALS ||
                currentToken.getToken() == GREATER_THAN_EQUALS ||
                currentToken.getToken() == NOT_EQUALS ||
                currentToken.getToken() == EQUALS) {

            if (expType == SymbolType.STRING &&
                    !(currentToken.getToken() == NOT_EQUALS || currentToken.getToken() == EQUALS))
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString());

            else if (expType == SymbolType.LOGICAL &&
                    !(currentToken.getToken() == NOT_EQUALS || currentToken.getToken() == EQUALS))
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString());

            logic_operators(); // casa token está dentro desse metodo


            SymbolType tempType = exp_sum();

            if (expType != tempType) {

                if (!((expType == SymbolType.INTEGER && tempType == SymbolType.BYTE) ||
                        (tempType == SymbolType.INTEGER && expType == SymbolType.BYTE)))
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString(), tempType.toString());
            }

            expType = SymbolType.LOGICAL;

        }

        return expType;
    }

    public SymbolType exp_sum() throws IOException, UnexpectedEndOfFileException, UnexpectedToken,
            UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        if (currentToken == null)
            return null;

        Token sign = null;
        Symbol symbol = currentToken;

        if (currentToken.getToken() == PLUS) {
            sign = currentToken.getToken();
            casaToken(PLUS);
        } else if (currentToken.getToken() == MINUS) {
            sign = currentToken.getToken();
            casaToken(MINUS);
        }

        SymbolType mainType = exp_product();

        if ((sign == PLUS || sign == MINUS) &&
                (mainType == SymbolType.STRING || mainType == SymbolType.LOGICAL))
            throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), mainType.toString());

        else if (sign == MINUS
                && symbol.getToken() == ID)
            mainType = SymbolType.INTEGER;


        while (currentToken.getToken() == PLUS ||
                currentToken.getToken() == MINUS ||
                currentToken.getToken() == OR) {

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

        return mainType;
    }

    public SymbolType exp_product() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        SymbolType mainType = exp_value();

        while (currentToken.getToken() == ASTERISK ||
                currentToken.getToken() == FORWARD_SLASH ||
                currentToken.getToken() == AND) {

            Token op = currentToken.getToken();

            switch (currentToken.getToken()) {
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

            SymbolType innerType = exp_value();

            if (mainType == SymbolType.STRING || innerType == SymbolType.STRING)
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                        mainType.toString(), innerType.toString());

            else if (op == FORWARD_SLASH || op == ASTERISK)
                mainType = SymbolType.INTEGER;

            else if (op == AND) {

                if (!(innerType == SymbolType.LOGICAL && mainType == SymbolType.LOGICAL))
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            mainType.toString(), innerType.toString());
            }
        }

        return mainType;
    }

    private SymbolType exp_value() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        SymbolType type = null;

        switch (currentToken.getToken()) {
            case LEFT_PARENTHESIS:
                casaToken(LEFT_PARENTHESIS);
                type = expression();
                casaToken(RIGHT_PARENTHESIS);
                break;
            case ID:

                if (currentToken.getClass_() == null)
                    throw new UnknownIdentifier(LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());
                else type = currentToken.getType();

                casaToken(ID);
                break;
            case NOT:
                casaToken(NOT);

                SymbolType expType = exp_value();

                if (expType != SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            SymbolType.LOGICAL.toString(), expType.toString());

                type = SymbolType.LOGICAL;
                break;
            case CONSTANT:
                type = currentToken.getType();
                casaToken(CONSTANT);
                break;
            case TRUE:
                type = SymbolType.LOGICAL;
                casaToken(TRUE);
                break;
            case FALSE:
                type = SymbolType.LOGICAL;
                casaToken(FALSE);
                break;
            default:
                throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(),
                        currentToken.getToken());

        }

        return type;
    }

    private SymbolType exp_value_const() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        SymbolType type = null;

        switch (currentToken.getToken()) {

            case NOT:
                casaToken(NOT);

                SymbolType expType = exp_value_const();

                if (expType != SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            SymbolType.LOGICAL.toString(), expType.toString());

                type = SymbolType.LOGICAL;
                break;
            case CONSTANT:
                type = currentToken.getType();
                casaToken(CONSTANT);
                break;
            case TRUE:
                type = SymbolType.LOGICAL;
                casaToken(TRUE);
                break;
            case FALSE:
                type = SymbolType.LOGICAL;
                casaToken(FALSE);
                break;
            default:
                throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(),
                        currentToken.getToken());

        }

        return type;
    }

}
