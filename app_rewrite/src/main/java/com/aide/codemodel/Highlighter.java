package com.aide.codemodel;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.common.AppLog;
import java.io.IOException;
import java.io.Reader;

public class Highlighter {
    private static final String LOG_TAG = "Highlighter";
    private final JFlexLexer lexer;

    public Highlighter(JFlexLexer lexer) {
        this.lexer = lexer;
    }

    public void highlight(FileEntry fileEntry, Reader reader, SyntaxTreeStyles syntaxTreeStyles) {
        AppLog.i(LOG_TAG, "highlight: " + lexer.getClass().getName());
        syntaxTreeStyles.clear(); // j6() -> DW()
        try {
            lexer.yyreset(reader);
            lexer.yybegin(lexer.getDefaultState());
            int style = lexer.yylex();

            int startLine = lexer.getLine() + 1;
            int startColumn = lexer.getColumn() + 1;
            while (true) {
                int nextStyle = lexer.yylex();
                int line = lexer.getLine() + 1;
                int column = lexer.getColumn() + 1;

				// 填充风格
                syntaxTreeStyles.addSyntaxTag(style, 0, startLine, startColumn, line, column);

				style = nextStyle;
                startLine = line;
                startColumn = column;
                if (nextStyle == -1) break;
                syntaxTreeStyles.addSyntaxTag(0, 0, startLine, startColumn, line, column);
            }
        }
		catch (IOException e) {
            AppLog.e(LOG_TAG, "highlight: " + lexer.getClass().getName(), e);
        }
		finally {
            try {
                lexer.yyclose();
            }
			catch (IOException ignored) {

            }
        }
    }

}
