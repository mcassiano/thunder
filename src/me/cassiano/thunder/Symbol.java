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

/* Classe Responsável pelo Armazenamento do Símbolo */

public class Symbol {

    private Token token;
    private String lexeme;
    private SymbolClass class_;
    private SymbolType type;

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

    public SymbolClass getClass_() {
        return class_;
    }

    public void setClass_(SymbolClass class_) {
        this.class_ = class_;
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }
}
