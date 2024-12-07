package com.aide.codemodel;

import java.io.IOException;
import java.io.Reader;

public interface JFlexLexer {
    int getDefaultState();
    
    int yystate();
    
    void yybegin(int state);
    
    void yyreset(Reader reader);
    
    int yylex() throws IOException;
    
    int getLine();
    
    int getColumn();
    
    void yyclose()throws IOException;
}
