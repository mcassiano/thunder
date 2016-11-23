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

    private final Buffer buffer;

    private Symbol currentToken;
    private PushbackInputStream fileStream;


    public Parser(PushbackInputStream fileStream, String outputFile) throws IOException {
        this.fileStream = fileStream;
        this.buffer = new Buffer(outputFile);
    }

    public void casaToken(Token tokenrecebido) throws IOException, UnexpectedEndOfFileException, UnexpectedToken, InvalidCharacterException, UnknownLexeme {


        if (currentToken != null && currentToken.getToken().equals(tokenrecebido)) {

            String messageFormat = "Token: %s, Lexeme: %s";
            String message = String.format(messageFormat, currentToken.getToken().name(),
                    currentToken.getLexeme());

//            System.out.println(message);

            currentToken = LexicalAnalyzer.get().analyze(fileStream);
        } else if (currentToken == null)
            throw new UnexpectedEndOfFileException(LexicalAnalyzer.get().getLineNumber());
        else
            throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(), currentToken.getToken());
    }

    public void start() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes, IncompatibleIdentifierClass, IdentifierInUse {


        //cabeçalho

        buffer.getBuffer().add("sseg SEGMENT STACK ;início seg. pilha");
        buffer.getBuffer().add("byte 16384 DUP(?) ;dimensiona pilha");
        buffer.getBuffer().add("sseg ENDS ;fim seg. pilha");
        buffer.getBuffer().add("dseg SEGMENT PUBLIC ;início seg. dados");
        buffer.getBuffer().add("byte 16384 DUP(?) ;temporários");

        this.currentToken = LexicalAnalyzer.get().analyze(fileStream); //le o primeiro token

        while (declaration()) {
            casaToken(SEMICOLON);
        }

        buffer.getBuffer().add("dseg ENDS ;fim seg. dados");
        buffer.getBuffer().add("cseg SEGMENT PUBLIC ;início seg. código");
        buffer.getBuffer().add("ASSUME CS:cseg, DS:dseg");
        buffer.getBuffer().add("strt:");
        buffer.getBuffer().add("mov ax, dseg");
        buffer.getBuffer().add("mov ds, ax");

        do {
            commands();
        } while (fileStream.available() != 0);

        buffer.getBuffer().add("mov ah, 4Ch");
        buffer.getBuffer().add("int 21h");
        buffer.getBuffer().add("cseg ENDS ;fim seg. código");
        buffer.getBuffer().add("END strt ;fim programa");

        buffer.dump();

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
            ExpressionReturn tempReturn = exp_value_const();
            tempID.setType(tempReturn.getType());
            tempID.setMemoryAddress(tempReturn.getAddress());

            switch (tempID.getType()) {

                case STRING:
                    buffer.getBuffer().add(String.format("byte \"%s$\" ; const string %s em %d",
                            tempReturn.getValue(), tempID.getLexeme(), tempID.getMemoryAddress()));
                    break;

                case BYTE:
                    buffer.getBuffer().add(String.format("byte %s ; const byte %s em %d",
                            tempReturn.getValue(), tempID.getLexeme(), tempID.getMemoryAddress()));
                    break;

                case INTEGER:
                    buffer.getBuffer().add(String.format("sword %s ; const int %s em %d",
                            tempReturn.getValue(), tempID.getLexeme(), tempID.getMemoryAddress()));
                    break;

                case LOGICAL:
                    buffer.getBuffer().add(String.format("byte %s ; const boolean %s em %d",
                            tempReturn.getValue(), tempID.getLexeme(), tempID.getMemoryAddress()));
                    break;
            }

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
            tempId.setMemoryAddress(MemoryManager.get().allocVariable(tempType));
            casaToken(ID);

            boolean hasAttribution = currentToken.getToken().equals(Token.ATTRIBUTION);

            if (hasAttribution) {

                casaToken(ATTRIBUTION);
                ExpressionReturn attrExpType = exp_value_const();

                switch (attrExpType.getType()) {

                    case STRING:
                        buffer.getBuffer().add(String.format("byte \"%s$\" ; const string %s em %d",
                                attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                        break;

                    case BYTE:
                        buffer.getBuffer().add(String.format("byte %s ; const byte %s em %d",
                                attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                        break;

                    case INTEGER:
                        buffer.getBuffer().add(String.format("sword %s ; const int %s em %d",
                                attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                        break;

                    case LOGICAL:
                        buffer.getBuffer().add(String.format("byte %s ; const boolean %s em %d",
                                attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                        break;
                }

                buffer.getBuffer().add(String.format("byte 256 DUP (?) ; var string %s em %d", tempId.getLexeme(), tempId.getMemoryAddress()));
                buffer.getBuffer().add(String.format("mov ax, ds:[%d]", attrExpType.getAddress()));
                buffer.getBuffer().add(String.format("mov ds:[%s], ax", tempId.getMemoryAddress()));

                if (attrExpType.getType() != tempType)
                    if (!(tempType == SymbolType.INTEGER && attrExpType.getType() == SymbolType.BYTE))
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString(), attrExpType.toString());

            }

            while (currentToken.getToken().equals(Token.COMMA)) {
                casaToken(COMMA);

                if (currentToken.getClass_() != null)
                    throw new IdentifierInUse(LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());

                tempId = currentToken;
                tempId.setClass_(SymbolClass.VAR);
                tempId.setType(tempType);
                tempId.setMemoryAddress(MemoryManager.get().allocVariable(tempType));

                casaToken(ID);

                if (currentToken.getToken().equals(Token.ATTRIBUTION)) {

                    casaToken(ATTRIBUTION);
                    ExpressionReturn attrExpType = exp_value_const();

                    switch (attrExpType.getType()) {

                        case STRING:
                            buffer.getBuffer().add(String.format("byte \"%s$\" ; const string %s em %d",
                                    attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                            break;

                        case BYTE:
                            buffer.getBuffer().add(String.format("byte %s ; const byte %s em %d",
                                    attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                            break;

                        case INTEGER:
                            buffer.getBuffer().add(String.format("sword %s ; const int %s em %d",
                                    attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                            break;

                        case LOGICAL:
                            buffer.getBuffer().add(String.format("byte %s ; const boolean %s em %d",
                                    attrExpType.getValue(), attrExpType.getValue(), attrExpType.getAddress()));
                            break;
                    }

                    buffer.getBuffer().add(String.format("byte 256 DUP (' ') ; var string %s em %d", tempId.getLexeme(), tempId.getMemoryAddress()));
                    buffer.getBuffer().add(String.format("mov ax, ds:[%d]", attrExpType.getAddress()));
                    buffer.getBuffer().add(String.format("mov ds:[%s], ax", tempId.getMemoryAddress()));

                    if (attrExpType.getType() != tempType)
                        if (!(tempType == SymbolType.INTEGER && attrExpType.getType() == SymbolType.BYTE))
                            throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString(), attrExpType.getType().toString());
                }
            }
        }
        return true;
    }

    public void commands() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes, IncompatibleIdentifierClass {

        if (currentToken == null)
            return;

        ExpressionReturn tempType;

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


                if (id.getType() != tempType.getType()) {

                    if (id.getType() == SymbolType.LOGICAL || tempType.getType() == SymbolType.LOGICAL)
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), id.getType().toString(), tempType.getType().toString());

                    if (!(id.getType() == SymbolType.INTEGER && tempType.getType() == SymbolType.BYTE))
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), id.getType().toString(), tempType.getType().toString());
                }


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

                boolean newLine = false;

                if (currentToken.getToken() == WRITE) {
                    casaToken(WRITE);
                } else {
                    newLine = true;
                    casaToken(WRITE_LINE);
                }

                casaToken(LEFT_PARENTHESIS);

                ExpressionReturn expressionReturn = expression();

                if (expressionReturn.getType() == SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expressionReturn.toString());

                buffer.getBuffer().add(String.format("mov dx, %d ; imprime", expressionReturn.getAddress()));
                buffer.getBuffer().add("mov ah, 09h ; imprime");
                buffer.getBuffer().add("int 21h ; imprime");

                if (newLine) {
                    buffer.getBuffer().add("mov ah, 02h ; new line");
                    buffer.getBuffer().add("mov dl, 0Dh ; new line");
                    buffer.getBuffer().add("int 21h ; new line");
                    buffer.getBuffer().add("mov DL, 0Ah ; new line");
                    buffer.getBuffer().add("int 21h ; new line");
                }


                while (currentToken.getToken() == COMMA) {
                    casaToken(COMMA);
                    tempType = expression();

                    if (tempType.getType() == SymbolType.LOGICAL)
                        throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), tempType.toString());
                }

                casaToken(RIGHT_PARENTHESIS);
                casaToken(SEMICOLON);
                break;

            case WHILE:
                casaToken(WHILE);
                casaToken(LEFT_PARENTHESIS);
                tempType = expression();

                if (tempType.getType() != SymbolType.LOGICAL)
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

                if (tempType.getType() != SymbolType.LOGICAL)
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

    public Token logic_operators() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, InvalidCharacterException, UnknownLexeme {

        Symbol temp = currentToken;


        switch (temp.getToken()) {
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

        return temp.getToken();
    }

    public ExpressionReturn expression() throws IOException, UnexpectedEndOfFileException, UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        ExpressionReturn expType = exp_sum();

        if (currentToken == null)
            return null;

        if (currentToken.getToken() == LESS_THAN ||
                currentToken.getToken() == GREATER_THAN ||
                currentToken.getToken() == LESS_THAN_EQUALS ||
                currentToken.getToken() == GREATER_THAN_EQUALS ||
                currentToken.getToken() == NOT_EQUALS ||
                currentToken.getToken() == EQUALS) {

            if (expType.getType() == SymbolType.STRING &&
                    !(currentToken.getToken() == NOT_EQUALS || currentToken.getToken() == EQUALS))
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString());

            else if (expType.getType() == SymbolType.LOGICAL &&
                    !(currentToken.getToken() == NOT_EQUALS || currentToken.getToken() == EQUALS))
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString());

            Token op = logic_operators(); // casa token está dentro desse metodo armazena tipo da op logica

            ExpressionReturn tempType = exp_sum();

            expType.setType(SymbolType.LOGICAL);
            expType.setAddress(MemoryManager.get().allocNewTemp(SymbolType.LOGICAL));

            if (expType.getType() != tempType.getType()) {

                if (!((expType.getType() == SymbolType.INTEGER && tempType.getType() == SymbolType.BYTE) ||
                        (tempType.getType() == SymbolType.INTEGER && expType.getType() == SymbolType.BYTE)))
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), expType.toString(), tempType.toString());
            }

            if (op == GREATER_THAN) {

                String labelTrue = LabelManager.get().newLabel();
                String labelEnd = LabelManager.get().newLabel();

                buffer.getBuffer().add("mov ax, ds:[" + expType.getAddress() + "]");
                buffer.getBuffer().add("mov bx, ds:[" + tempType.getAddress() + "]");
                buffer.getBuffer().add("cmp ax, bx");
                buffer.getBuffer().add("jg " + labelTrue);
                buffer.getBuffer().add("mov al, 0");
                buffer.getBuffer().add("jmp " + labelEnd);
                buffer.getBuffer().add(labelTrue + ":");
                buffer.getBuffer().add("	mov al, 0ffh");
                buffer.getBuffer().add(labelEnd + ":");
                buffer.getBuffer().add("	mov ds:[" + expType.getAddress() + "], al");

            } else if (op == LESS_THAN) {

                buffer.getBuffer().add("mov ah, 0");
                buffer.getBuffer().add("mov bh, 0");
                buffer.getBuffer().add("mov ax, ds:[" + expType.getAddress() + "]");
                buffer.getBuffer().add("mov bx, ds:[" + tempType.getAddress() + "]");
                buffer.getBuffer().add("cmp ax, bx");

                String labelTrue = LabelManager.get().newLabel();
                String labelEnd = LabelManager.get().newLabel();

                buffer.getBuffer().add("jl " + labelTrue);
                buffer.getBuffer().add("mov al, 0");

                buffer.getBuffer().add("jmp " + labelEnd);
                buffer.getBuffer().add(labelTrue + ":");
                buffer.getBuffer().add("mov al, 0ffh");
                buffer.getBuffer().add(labelEnd + ":");
                buffer.getBuffer().add("	mov ds:[" + expType.getAddress() + "], al");

            } else if (op == GREATER_THAN_EQUALS) {

                buffer.getBuffer().add("mov ah, 0");
                buffer.getBuffer().add("mov bh, 0");
                buffer.getBuffer().add("mov ax, ds:[" + expType.getAddress() + "]");
                buffer.getBuffer().add("mov bx, ds:[" + tempType.getAddress() + "]");
                buffer.getBuffer().add("cmp ax, bx");

                String rotVerdadeiro = LabelManager.get().newLabel();

                buffer.getBuffer().add("jge R" + rotVerdadeiro);
                buffer.getBuffer().add("mov al, 0");

                String rotFim = LabelManager.get().newLabel();

                buffer.getBuffer().add("jmp R" + rotFim);
                buffer.getBuffer().add(rotVerdadeiro + ":");
                buffer.getBuffer().add("mov al, 0ffh");
                buffer.getBuffer().add(rotFim + ":");
                buffer.getBuffer().add("mov ds:[" + expType.getAddress() + "], al");

            } else if (op == LESS_THAN_EQUALS) {

                buffer.getBuffer().add("mov ah, 0");
                buffer.getBuffer().add("mov bh, 0");
                buffer.getBuffer().add("mov ax, ds:[" + expType.getAddress() + "]");
                buffer.getBuffer().add("mov bx, ds:[" + tempType.getAddress() + "]");
                buffer.getBuffer().add("cmp ax, bx");

                String rotVerdadeiro = LabelManager.get().newLabel();

                buffer.getBuffer().add("jle " + rotVerdadeiro);
                buffer.getBuffer().add("mov al, 0");

                String rotFim = LabelManager.get().newLabel();

                buffer.getBuffer().add("jmp " + rotFim);
                buffer.getBuffer().add(rotVerdadeiro + ":");
                buffer.getBuffer().add("mov al, 0ffh");
                buffer.getBuffer().add(rotFim + ":");
                buffer.getBuffer().add("mov ds:[" + expType.getAddress() + "], al");


            }

        }

        return expType;
    }

    public ExpressionReturn exp_sum() throws IOException, UnexpectedEndOfFileException, UnexpectedToken,
            UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        if (currentToken == null)
            return null;

        ExpressionReturn expressionReturn = new ExpressionReturn();
        Token sign = null;

        if (currentToken.getToken() == PLUS || currentToken.getToken() == MINUS) {
            sign = currentToken.getToken();
            casaToken(currentToken.getToken());
        }

        ExpressionReturn mainType = exp_product();

        if ((sign == PLUS || sign == MINUS) &&
                (mainType.getType() == SymbolType.STRING || mainType.getType() == SymbolType.LOGICAL))
            throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(), mainType.toString());

        else if (sign == MINUS) {

            expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.INTEGER));
            expressionReturn.setType(SymbolType.INTEGER);

            buffer.getBuffer().add(String.format("mov ax, ds:[%s] ; inverte sinal", mainType.getAddress()));
            buffer.getBuffer().add("neg ax ; inverte sinal");
            buffer.getBuffer().add(String.format("mov ds:[%s], ax ; inverte sinal", expressionReturn.getAddress()));
        } else {
            expressionReturn.setType(mainType.getType());
            buffer.getBuffer().add(String.format("mov ax, ds:[%s] ; expressao", mainType.getAddress()));
            buffer.getBuffer().add(String.format("mov ds:[%s], ax ; expressao", expressionReturn.getAddress()));
        }


        while (currentToken.getToken() == PLUS ||
                currentToken.getToken() == MINUS ||
                currentToken.getToken() == OR) {

            Token op = currentToken.getToken();

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

            ExpressionReturn innerExpReturn = exp_product();

            if (op != PLUS && (expressionReturn.getType() == SymbolType.STRING || innerExpReturn.getType() == SymbolType.STRING))
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                        expressionReturn.getType().toString(), innerExpReturn.getType().toString());

            if (innerExpReturn.getType() != mainType.getType()) {

                if (innerExpReturn.getType() == SymbolType.STRING || expressionReturn.getType() == SymbolType.STRING)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            expressionReturn.getType().toString(), innerExpReturn.getType().toString());

                expressionReturn.setType(SymbolType.INTEGER);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.INTEGER));

            }

            if (op == PLUS) {


                switch (expressionReturn.getType()) {

                    case STRING:
                        buffer.getBuffer().add(String.format("mov ax, ds:[%d]", expressionReturn.getAddress()));
                        buffer.getBuffer().add(String.format("mov bx, ds:[%d]", innerExpReturn.getAddress()));
                        buffer.getBuffer().add("add ax, bx");
                        buffer.getBuffer().add(String.format("mov ds:[%d], ax", expressionReturn.getAddress()));
                        break;

                    case BYTE:
                        buffer.getBuffer().add(String.format("mov al, ds:[%d]", expressionReturn.getAddress()));
                        buffer.getBuffer().add(String.format("mov bl, ds:[%d]", innerExpReturn.getAddress()));
                        buffer.getBuffer().add("add bl, al");
                        buffer.getBuffer().add(String.format("mov ds:[%d], bl", expressionReturn.getAddress()));
                        break;

                    case INTEGER:
                        buffer.getBuffer().add(String.format("mov ax, ds:[%d]", expressionReturn.getAddress()));
                        buffer.getBuffer().add("mov bh, 0");
                        buffer.getBuffer().add(String.format("mov bx, ds:[%s]", innerExpReturn.getAddress()));
                        buffer.getBuffer().add("add ax, bx");
                        buffer.getBuffer().add(String.format("mov ds:[%d], ax", expressionReturn.getAddress()));
                        break;
                }


            }

            if (op == MINUS) {


                switch (expressionReturn.getType()) {

                    case BYTE:
                        buffer.getBuffer().add(String.format("mov al, ds:[%d]", expressionReturn.getAddress()));
                        buffer.getBuffer().add(String.format("mov bl, ds:[%d]", innerExpReturn.getAddress()));
                        buffer.getBuffer().add("sub bl, al");
                        buffer.getBuffer().add(String.format("mov ds:[%d], bl", expressionReturn.getAddress()));
                        break;

                    case INTEGER:
                        buffer.getBuffer().add(String.format("mov ax, ds:[%d]", expressionReturn.getAddress()));
                        buffer.getBuffer().add("mov bh, 0");
                        buffer.getBuffer().add(String.format("mov bx, ds:[%s]", innerExpReturn.getAddress()));
                        buffer.getBuffer().add("sub ax, bx");
                        buffer.getBuffer().add(String.format("mov ds:[%d], ax", expressionReturn.getAddress()));
                        break;
                }


            }


        }

        return expressionReturn;
    }

    public ExpressionReturn exp_product() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        ExpressionReturn expressionReturn = exp_value();

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

            ExpressionReturn innerType = exp_value();

            if (expressionReturn.getType() == SymbolType.STRING || innerType.getType() == SymbolType.STRING)
                throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                        expressionReturn.toString(), innerType.toString());

            else if (op == AND) {

                if (!(innerType.getType() == SymbolType.LOGICAL && expressionReturn.getType() == SymbolType.LOGICAL))
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            expressionReturn.toString(), innerType.toString());
            } else if (op == FORWARD_SLASH) {
                expressionReturn.setType(SymbolType.INTEGER);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.INTEGER));

                buffer.getBuffer().add(String.format("mov ax, ds:[%d] ; divisao", expressionReturn.getAddress()));
                buffer.getBuffer().add(String.format("mov bx, ds:[%d] ; divisao", innerType.getAddress()));
                buffer.getBuffer().add("div ax, bx ; divisao");
                buffer.getBuffer().add(String.format("mov ds:[%d], ax ; divisao", expressionReturn.getAddress()));
            } else if (op == ASTERISK) {

                expressionReturn.setType(SymbolType.INTEGER);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.INTEGER));

                buffer.getBuffer().add(String.format("mov al, ds:[%d] ; multiplicao", expressionReturn.getAddress()));
                buffer.getBuffer().add(String.format("mov bl, ds:[%d] ; multiplicao", innerType.getAddress()));
                buffer.getBuffer().add("mul bl ; multiplicao");
                buffer.getBuffer().add(String.format("mov ds:[%d], ax ; multiplicao", expressionReturn.getAddress()));
            }
        }

        return expressionReturn;
    }

    private ExpressionReturn exp_value() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        ExpressionReturn expressionReturn = new ExpressionReturn();

        switch (currentToken.getToken()) {
            case LEFT_PARENTHESIS:
                casaToken(LEFT_PARENTHESIS);

                ExpressionReturn innerExp = expression();
                expressionReturn.setType(innerExp.getType());
                expressionReturn.setAddress(innerExp.getAddress());

                casaToken(RIGHT_PARENTHESIS);
                break;

            case ID:

                if (currentToken.getClass_() == null)
                    throw new UnknownIdentifier(LexicalAnalyzer.get().getLineNumber(), currentToken.getLexeme());
                else {
                    expressionReturn.setType(currentToken.getType());
                    expressionReturn.setAddress(currentToken.getMemoryAddress());
                }

                casaToken(ID);
                break;

            case NOT:
                casaToken(NOT);

                ExpressionReturn innerExpNot = expression();

                if (innerExpNot.getType() != SymbolType.LOGICAL)
                    throw new IncompatibleTypes(LexicalAnalyzer.get().getLineNumber(),
                            SymbolType.LOGICAL.toString(), innerExpNot.toString());

                expressionReturn.setType(SymbolType.LOGICAL);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.LOGICAL));

                buffer.getBuffer().add(String.format("mov al, ds:[%d] ; nega bool", innerExpNot.getAddress()));
                buffer.getBuffer().add("neg al ; nega bool");
                buffer.getBuffer().add(String.format("mov ds:[%d], al ; nega bool", expressionReturn.getAddress()));

                break;

            case CONSTANT:

                int constantTempAddress;

                if (currentToken.getType() == SymbolType.STRING) {

                    constantTempAddress = MemoryManager.get().allocNewTemp(SymbolType.STRING,
                            currentToken.getLexeme().length() + 1);
                } else
                    constantTempAddress = MemoryManager.get().allocNewTemp(currentToken.getType());

                expressionReturn.setType(currentToken.getType());
                expressionReturn.setAddress(constantTempAddress);
                buffer.getBuffer().add(String.format("mov ax, %s ; move const", currentToken.getLexeme()));
                buffer.getBuffer().add(String.format("mov ds:[%s], ax ; move const", expressionReturn.getAddress()));
                casaToken(CONSTANT);
                break;
            case TRUE:
                expressionReturn.setType(SymbolType.LOGICAL);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.LOGICAL));
                buffer.getBuffer().add(String.format("mov ds:[%s], 0ffh ; boolean true", expressionReturn.getAddress()));
                casaToken(TRUE);
                break;
            case FALSE:
                expressionReturn.setType(SymbolType.LOGICAL);
                expressionReturn.setAddress(MemoryManager.get().allocNewTemp(SymbolType.LOGICAL));
                buffer.getBuffer().add(String.format("mov ds:[%s], 0 ; boolean false", expressionReturn.getAddress()));
                casaToken(FALSE);
                break;
            default:
                throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(),
                        currentToken.getToken());

        }

        return expressionReturn;
    }

    private ExpressionReturn exp_value_const() throws IOException, UnexpectedEndOfFileException,
            UnexpectedToken, UnknownLexeme, InvalidCharacterException, UnknownIdentifier, IncompatibleTypes {

        ExpressionReturn expressionReturn = new ExpressionReturn();
        Integer newTempAddress;

        String newLexeme = "";

        if (currentToken.getToken() == MINUS ||
                currentToken.getToken() == PLUS) {

            newLexeme += currentToken.getLexeme();
            casaToken(currentToken.getToken());
            currentToken.setType(SymbolType.INTEGER);
        }

        switch (currentToken.getToken()) {

            case CONSTANT:

                if (currentToken.getType() == SymbolType.STRING) {

                    int size = currentToken.getLexeme().length() + 1;
                    newTempAddress = MemoryManager.get().allocVariable(currentToken.getType(), size);

                    expressionReturn.setType(currentToken.getType());
                    expressionReturn.setAddress(newTempAddress);
                    expressionReturn.setValue(currentToken.getLexeme());
                    casaToken(CONSTANT);

                } else {

                    newTempAddress = MemoryManager.get().allocVariable(currentToken.getType());
                    expressionReturn.setType(currentToken.getType());
                    expressionReturn.setAddress(newTempAddress);
                    newLexeme += currentToken.getLexeme();
                    expressionReturn.setValue(newLexeme);
                    casaToken(CONSTANT);
                }
                break;

            case TRUE:
                newTempAddress = MemoryManager.get().allocVariable(SymbolType.LOGICAL);
                expressionReturn.setAddress(newTempAddress);
                expressionReturn.setType(SymbolType.LOGICAL);
                expressionReturn.setValue("0ffh");
                casaToken(TRUE);
                break;

            case FALSE:
                newTempAddress = MemoryManager.get().allocVariable(SymbolType.LOGICAL);
                expressionReturn.setAddress(newTempAddress);
                expressionReturn.setType(SymbolType.LOGICAL);
                expressionReturn.setValue("0");
                casaToken(FALSE);
                break;


            default:
                throw new UnexpectedToken(LexicalAnalyzer.get().getLineNumber(),
                        currentToken.getToken());

        }

        return expressionReturn;
    }

}
