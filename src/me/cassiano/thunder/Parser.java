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

/*
* START → {[DECLARATION];}+ {[COMMAND];}+
*
* DECLARATION → (final | int | boolean | string)id[<- const]{id[<- const]}*
*
* COMMAND →	id <- EXPRESSION |
* while (EXPRESSION) (COMMAND |  begin {COMMAND;}* endwhile) |
* if (EXPRESSION) (COMMAND |  begin {COMMAND;}* endif) [else (COMMAND |  begin {COMMAND;}* endelse)] |
* readln (id) |
* write (EXPRESSION {, EXPRESSION}*) |
* writeln (EXPRESSION {, EXPRESSION}*)
*
* LOGIC_COMPARATION → (>|<|<=|>=|!=|==)
*
* EXPRESSION → Exp_SUM [LOGIC_COMPARATION Exp_SUM]
* Exp_SUM → [+|-] Exp_PRODUCT {(+|-|OR) Exp_PRODUCT}*
* Exp_PRODUCT → Exp_VALUE {(*|/|AND) Exp_VALUE}*
* Exp_VALUE → "(" EXPRESSION ")" | id | const | NOT Exp_VALUE
* */

public class Parser {

    //private LexicalAnalyzer lexAn;
    private Symbol currentToken;
    private String lexema;
    private PushbackInputStream fileStream;
    public static long linenumber;

    public Parser(PushbackInputStream fileStream) throws IOException {
        this.fileStream=fileStream;
        this.currentToken = LexicalAnalyzer.get().analyze(fileStream); //le o primeiro token
        System.out.println(currentToken.getLexeme());
    }

    public void casaToken (Token tokenrecebido) throws IOException {

        if(currentToken.equals(tokenrecebido)){
            currentToken = LexicalAnalyzer.get().analyze(fileStream);
        }//else erro

        /*
        * se tokenAtual = tokenRecebido
        *   chama Analisador Léxico
        * senão ERRO
        *
        * if(tokenRecebido != (byte)registro.getNumToken())
		{
			if(registro.getNumToken()==(byte)65535)
			{
				System.out.println(registro.getCont()+":fim de arquivo não esperado.");
				System.exit(0);
			}
			System.out.println(registro.getCont()+":token não esperado.");
			System.exit(0);

		}else{
			registro = anLex.automato(registro.getMarcado(), registro.getC());
		}
        *
        * */
    }

    public void run(PushbackInputStream fileStream) throws IOException {
        this.currentToken = LexicalAnalyzer.get().analyze(fileStream);
    }

    public void start() throws IOException {
        //chamar declaration ;
        //chamar commands;
    }

    public void imprimeToken(Token token){
        System.out.println(token.toString());
    }

    public void declaration() throws IOException {

        switch (currentToken.getToken()){
            case FINAL:
                casaToken(FINAL);
                break;
            case INT:
                casaToken(INT);
                break;
            case BOOLEAN:
                casaToken(BOOLEAN);
                break;
            case STRING:
                casaToken(STRING);
                break;
        }

    }


}
