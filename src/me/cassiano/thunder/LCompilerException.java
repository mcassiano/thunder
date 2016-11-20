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


/* Classe Responsável pelo Tratamento de Excecoes do Compilador */


class UnexpectedEndOfFileException extends Exception {

    private String message;

    UnexpectedEndOfFileException(int line) {
        super();

        String messageFormat = "Linha %d: Fim de arquivo não esperado.";
        message = String.format(messageFormat, line);
    }

    @Override
    public String getMessage() {
        return message;
    }
}


class InvalidCharacterException extends Exception {

    private String message;

    InvalidCharacterException(int line, String char_) {
        super();

        String messageFormat = "Linha %d: Caracter inválido (%s).";
        message = String.format(messageFormat, line, char_);
    }

    @Override
    public String getMessage() {
        return message;
    }
}

class UnexpectedToken extends Exception {

    private String message;

    UnexpectedToken(int line, Token token) {
        super();

        String messageFormat = "Linha %d: Token %s não esperado [%s].";
        message = String.format(messageFormat, line, token.name(), token.toString());
    }

    @Override
    public String getMessage() {
        return message;
    }
}


class UnknownLexeme extends Exception {

    private String message;

    UnknownLexeme(int line, String lexeme) {
        super();

        String messageFormat = "Linha %d: Lexema (%s) não encontrado.";
        message = String.format(messageFormat, line, lexeme);
    }

    @Override
    public String getMessage() {
        return message;
    }
}

class UnknownIdentifier extends Exception {

    private String message;

    UnknownIdentifier(int line, String lexeme) {
        super();

        String messageFormat = "Linha %d: Identificador (%s) não declarado.";
        message = String.format(messageFormat, line, lexeme);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

class IncompatibleTypes extends Exception {

    private String message;

    IncompatibleTypes(int line, String expected, String actual) {
        super();

        String messageFormat = "Linha %d: Tipos incompatíveis ('%s' e '%s').";
        message = String.format(messageFormat, line, expected, actual);
    }

    IncompatibleTypes(int line, String type) {
        super();

        String messageFormat = "Linha %d: Tipo incompatível (%s).";
        message = String.format(messageFormat, line, type);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

class IncompatibleIdentifierClass extends Exception {

    private String message;

    IncompatibleIdentifierClass(int line, String lexeme) {
        super();

        String messageFormat = "Linha %d: Classe de identificador incompatível ('%s').";
        message = String.format(messageFormat, line, lexeme);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

class IdentifierInUse extends Exception {

    private String message;

    IdentifierInUse(int line, String lexeme) {
        super();

        String messageFormat = "Linha %d: Identificador já declarado ('%s').";
        message = String.format(messageFormat, line, lexeme);
    }

    @Override
    public String getMessage() {
        return message;
    }

}