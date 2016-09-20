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
 */

package me.cassiano.thunder;


import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.regex.Pattern;

import me.cassiano.thunder.exception.UnexpectedEndOfFile;
//import sun.jvm.hotspot.debugger.cdbg.Sym;

/* Classe responsável pela Análise Léxica */
public class LexicalAnalyzer {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_WIN = "\r";
    private static final String TAB = "\t";

    private static final String PIPE = "|";
    private static final String AMPERSAND = "&";
    private static final String QUOTE = "\"";

    private static final LexicalAnalyzer instance = new LexicalAnalyzer();

    public static LexicalAnalyzer get() {
        return instance;
    }

    public Symbol analyze(PushbackInputStream fileStream) throws IOException {

        State state = State.Q_START;
        Symbol sym = null;
        String lexeme = "";
        Integer lineNumber = 0;

        while (state != State.Q_END) {

            String currentChar;

            try {
                currentChar = readChar(fileStream);
            } catch (UnexpectedEndOfFile unexpectedEndOfFile) {
                return new Symbol(Token.EOF);
            }

            if (state == State.Q_START &&
                    isBlankChar(currentChar))
                continue;

            if (state == State.Q_START &&
                    isNewLine(currentChar)) {
                lineNumber += 1;
                continue;
            }

            switch (state) {

                case Q_START:
                    ProcessingResponse response = processQStart(currentChar);
                    state = response.getStateResponse();
                    lexeme += response.getLexeme();
                    sym = response.getSymbol();
                    break;

                case Q_1:
                    /* current state: Q1, if next char is *, move to Q2 otherwise, return Token */
                    if (Token.fromString(currentChar) != Token.ASTERISK) {
                        fileStream.unread(currentChar.charAt(0));
                        sym = SymbolTable.get().getSymbol(currentChar);
                        state = State.Q_END;
                    }
                    else
                        state = State.Q_2;
                    break;

                case Q_2:
                    /* current state: Q2, if next char is not *, just keep looping here */

                    if (Token.fromString(currentChar) != Token.ASTERISK)
                        state = State.Q_2;
                    else
                        state = State.Q_3;

                    break;

                case Q_3:

                    if (Token.fromString(currentChar) == Token.ASTERISK)
                        state = State.Q_3;

                    else if (Token.fromString(currentChar) != Token.FORWARD_SLASH &&
                            Token.fromString(currentChar) != Token.ASTERISK)
                        state = State.Q_2;

                    else if (Token.fromString(currentChar) == Token.FORWARD_SLASH)
                        state = State.Q_START;

                    break;

                case Q_4:
                    /* current state: Q4, a lexeme+currentChar is either != or >=
                     * otherwise just return the latest symbol */

                    if (SymbolTable.get().hasSymbol(lexeme + currentChar)) {
                        lexeme += currentChar;
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    } else {
                        fileStream.unread(currentChar.charAt(0));
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    }

                    break;

                case Q_5:
                    /* current state: Q5, a lexeme+currentChar is either <- or <=
                     * otherwise just return the latest symbol */

                    if (SymbolTable.get().hasSymbol(lexeme + currentChar)) {
                        lexeme += currentChar;
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    } else {
                        fileStream.unread(currentChar.charAt(0));
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    }

                    break;


                case Q_6:
                    /* current state: Q6, if currentChar is PIPE, then return Token OR ("||") */
                    if (currentChar.equals(PIPE)) {
                        lexeme += currentChar;
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    }
                    break;

                case Q_7:
                    /* current state: Q7, if currentChar is AMPERSAND, then return Token AND ("&&") */
                    if (currentChar.equals(AMPERSAND)) {
                        lexeme += currentChar;
                        sym = SymbolTable.get().getSymbol(lexeme);
                        state = State.Q_END;
                    }
                    break;

                case Q_8:
                    /* current state: Q8, read the ID */

                    if (isLetterDigitOrUnderscore(currentChar)) {
                        lexeme += currentChar;
                        state = State.Q_8;
                    }
                    else {
                        fileStream.unread(currentChar.charAt(0));

                        /* is reserved word or symbol has been seen? */
                        if (SymbolTable.get().hasSymbol(lexeme))
                            sym = SymbolTable.get().getSymbol(lexeme);
                        else
                            sym = new Symbol(Token.ID, lexeme);

                        state = State.Q_END;
                    }

                    break;
                case Q_9:
                    /* current state: Q9, already read the first quote in String */

                    if(currentChar.equals(QUOTE)) { // FALTANDO estado de QUEBRA de linha
                        lexeme += currentChar;
                        state = State.Q_END;
                        sym = new Symbol(Token.STRING_LITERAL, lexeme);
                    } else{
                        lexeme += currentChar;
                        state = State.Q_10;

                    }

                    break;

                case Q_10:
                    /* current state: Q10, read value of String between quotes */

                    if(!currentChar.equals(QUOTE)){ // FALTANDO estado de QUEBRA de linha
                        lexeme+= currentChar;
                        state = State.Q_10;
                    } else{
                        lexeme += currentChar;
                        sym = new Symbol(Token.STRING_LITERAL, lexeme);
                        state = State.Q_END;
                    }

                    break;

                case Q_11:
                    /* current state: Q11 after reading '0', read value of a constant, HEX ou decimal */

                    if(isDigit(currentChar)){
                        lexeme += currentChar;
                        state = State.Q_14;
                    } else if( currentChar.equals("x")){
                        lexeme += currentChar;
                        state = State.Q_12;
                    } else{
                        fileStream.unread(currentChar.charAt(0));
                        state = State.Q_END;
                        sym = new Symbol(Token.CONSTANT, lexeme);

                    }


                    break;

                case Q_12:
                    /* current state: Q12 , read a HEX digit  */

                    if(isHex(currentChar)){
                        lexeme += currentChar;
                        state = State.Q_13;
                    }

                    break;

                case Q_13:
                    /* current state Q13, read the second HEX digit */

                    if(isHex(currentChar)){
                        lexeme += currentChar;
                        state = State.Q_END;
                        sym = new Symbol(Token.CONSTANT_HEX, lexeme);

                    }

                    break;

                case Q_14:
                    /*current state Q14, read digit
                    * otherwise just return the latest symbol */

                    if(isDigit(currentChar)){
                        lexeme += currentChar;
                        state = State.Q_14;
                    } else {
                        fileStream.unread(currentChar.charAt(0));
                        state = State.Q_END;
                        sym = new Symbol(Token.CONSTANT, lexeme);

                    }

                    break;
            }


        }

        return sym;
    }

    private ProcessingResponse processQStart(String currentChar) {

        Token token = Token.fromString(currentChar);
        State state = State.Q_START;
        String lexeme = "";

        if (token == null) {

            if (currentChar.equals(PIPE)) {
                lexeme += currentChar;
                state = State.Q_6;
            } else if (currentChar.equals(AMPERSAND)) {
                lexeme += currentChar;
                state = State.Q_7;
            } else if (isLetterOrUnderscore(currentChar)) {
                lexeme += currentChar;
                state = State.Q_8;
            } else if (currentChar.equals(QUOTE)) {
                lexeme += currentChar;
                state = State.Q_9;
            } else if (currentChar.equals("0")){ // nao esta lendo o digito 0 sozinho
                lexeme += currentChar;
                state = State.Q_11;
            } else if (Pattern.matches("[1-9]",currentChar) ){
                lexeme +=currentChar;
                state = State.Q_14;
            }

        } else {

            switch (token) {

                case EQUALS:
                case RIGHT_PARENTHESIS:
                case LEFT_PARENTHESIS:
                case COMMA:
                case PLUS:
                case ASTERISK:
                case SEMICOLON:
                    Symbol sym = new Symbol(token, currentChar);
                    state = State.Q_END;
                    return new ProcessingResponse(state, lexeme, sym);

                case NOT:
                case GREATER_THAN:
                    lexeme += currentChar;
                    state = State.Q_4;
                    break;

                case FORWARD_SLASH:
                    state = State.Q_1;
                    break;

                case LESS_THAN:
                    lexeme += currentChar;
                    state = State.Q_5;
                    break;

                case UNDERSCORE:
                    lexeme += currentChar;
                    state = State.Q_8;
                    break;

            }
        }


        return new ProcessingResponse(state, lexeme, null);

    }

    private String readChar(PushbackInputStream fileStream) throws IOException, UnexpectedEndOfFile {

        int _char;
        String tChar;

        _char = fileStream.read();

        if (_char == -1)
            throw new UnexpectedEndOfFile();

        tChar = String.valueOf((char) _char);

        return tChar;
    }

    private boolean isBlankChar(String str) {

        return str.isEmpty() ||
                str.equals(TAB) ||
                str.equals(BLANK);
    }

    private boolean isNewLine(String str) {

        return str.equals(NEW_LINE) ||
                str.equals(NEW_LINE_WIN);
    }

    private boolean isLetterOrUnderscore(String str) {

        String pattern = "[A-Za-z_]";
        return Pattern.matches(pattern, str);

    }

    private boolean isLetterDigitOrUnderscore(String str) {

        String pattern = "[A-Za-z0-9_]";
        return Pattern.matches(pattern, str);
    }

    private boolean isDigit(String str){
        String pattern = "[0-9]";
        return Pattern.matches(pattern, str);
    }

    private boolean isHex(String str){
        String pattern = "[A-F0-9]";
        return Pattern.matches(pattern, str);
    }

    enum State {

        Q_START,
        Q_1,
        Q_2,
        Q_3,
        Q_4,
        Q_5,
        Q_6,
        Q_7,
        Q_8,
        Q_9,
        Q_10,
        Q_11,
        Q_12,
        Q_13,
        Q_14,
        Q_END
    }

    private class ProcessingResponse {

        private State stateResponse;
        private String lexeme;
        private Symbol symbol;

        public ProcessingResponse(State stateResponse, String lexeme, Symbol symbol) {
            this.stateResponse = stateResponse;
            this.lexeme = lexeme;
            this.symbol = symbol;
        }

        public State getStateResponse() {
            return stateResponse;
        }

        public String getLexeme() {
            return lexeme;
        }

        public Symbol getSymbol() {
            return symbol;
        }
    }

}
