package me.cassiano.thunder;

import java.io.FileInputStream;

/**
 * Created by mateus on 11/09/16.
 */
public class Parser {

    private LexicalAnalyzer lexAn;
    private Symbol currentToken;
    private String lexema;

    public Parser() {
        this.lexema = "";
    }


//System.out.println("");



    public void run(FileInputStream fileStream){
        //this.currentToken = lexAn.analyze(fileStream).g;

    }
}
