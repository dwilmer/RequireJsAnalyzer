package analyzer;

import model.RequireJsModule;

public interface FileAnalyzer {
	public void analyzeLine(String line, int lineNumber, RequireJsModule module);
}
