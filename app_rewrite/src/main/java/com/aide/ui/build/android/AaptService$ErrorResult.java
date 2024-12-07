package com.aide.ui.build.android;

import com.aide.engine.SyntaxError;
import java.util.List;
import java.util.Map;

public class AaptService$ErrorResult {
	// error info
    public String errorInfo;
    public Map<String, List<SyntaxError>> syntaxErrorsMap;
	// hasError
    public boolean hasError;
	
    public AaptService$ErrorResult(String error) {
		this.errorInfo = error;
	}
    public AaptService$ErrorResult(boolean hasError) {
		this.hasError = hasError;
    }
}

