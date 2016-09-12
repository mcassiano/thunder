package me.cassiano.thunder;


import java.io.FileInputStream;
import java.io.IOException;

import me.cassiano.thunder.exception.UnexpectedEndOfFile;

import static me.cassiano.thunder.LexicalAnalyzer.State.Q_1;
import static me.cassiano.thunder.LexicalAnalyzer.State.Q_2;
import static me.cassiano.thunder.LexicalAnalyzer.State.Q_3;
import static me.cassiano.thunder.LexicalAnalyzer.State.Q_4;
import static me.cassiano.thunder.LexicalAnalyzer.State.Q_END;
import static me.cassiano.thunder.LexicalAnalyzer.State.Q_START;

public class LexicalAnalyzer {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_WIN = "\r";
    private static final String TAB = "\t";

    private static final String PIPE = "|";

    private static final LexicalAnalyzer instance = new LexicalAnalyzer();

    public static LexicalAnalyzer get() {
        return instance;
    }

    public Symbol analyze(FileInputStream fileStream) throws IOException {

        State state = State.Q_START;
        String lexeme = "";
        Symbol sym = null;

        while (state != State.Q_END) {

            if (state == State.Q_START)
                lexeme = "";

            String charRead;

            try {
                charRead = readChar(fileStream);
            } catch (UnexpectedEndOfFile unexpectedEndOfFile) {
                return new Symbol(Token.EOF);
            }

            if (shouldIgnore(charRead))
                continue;

            lexeme += charRead;

            if (SymbolTable.get().hasSymbol(lexeme)) {

                sym = SymbolTable.get().getSymbol(lexeme);

                switch (sym.getToken()) {

                    case EQUALS:
                    case LEFT_PARENTHESIS:
                    case RIGHT_PARENTHESIS:
                    case APOSTROPHE:
                    case PLUS:
                    case MINUS:
                    case ASTERISK:
                    case SEMICOLON:
                    case UNDERSCORE:
                    case OR:
                        state = State.Q_END;
                        break;

                    case FORWARD_SLASH:
                        state = Q_1; // pre-process comment
                        break;
                }
            }

            else {

                Token tmp = Token.fromString(charRead);

                if (tmp == null) {

                    switch (charRead) {
                        case PIPE:
                            state = Q_4;
                            continue;
                    }
                }

                switch (state) {

                    case Q_1:
                        if (tmp != Token.ASTERISK) {
                            sym = new Symbol(Token.ASTERISK);
                            state = Q_END;
                        }
                        else
                            state = Q_2;

                        break;

                    case Q_2:
                        if (tmp != Token.ASTERISK)
                            state = Q_2;
                        else
                            state = Q_3;

                        break;

                    case Q_3:
                        if (tmp == Token.FORWARD_SLASH)
                            state = Q_START;
                        else if (tmp == Token.ASTERISK)
                            state = Q_3;
                        else
                            state = Q_2;
                        break;
                }

            }

        }


        return sym;
    }

    private String readChar(FileInputStream fileStream) throws IOException, UnexpectedEndOfFile {

        int _char;
        String tChar;

        _char = fileStream.read();

        if (_char == -1)
            throw new UnexpectedEndOfFile();

        tChar = String.valueOf((char) _char);

        return tChar;
    }

    private boolean shouldIgnore(String str) {

        return  str.isEmpty() ||
                str.equals(NEW_LINE) ||
                str.equals(NEW_LINE_WIN) ||
                str.equals(TAB) ||
                str.equals(BLANK);
    }

    enum State {

        Q_START,
        Q_1,
        Q_2,
        Q_3,
        Q_4,
        Q_END
    }

}
